package com.project.smartmatch.service;

import com.project.smartmatch.model.response.EnhanceCVResponse;
import com.project.smartmatch.model.entity.CandidateProfile;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.repository.CandidateProfileRepository;
import com.project.smartmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EnhanceCVService {

    private final GeminiService geminiService;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final StringRedisTemplate redisTemplate; // Redis işlemleri için enjekte ettik

    // Geri dönüş tipini hem kalan hak sayısını hem de cevabı kontrolcüye taşımak için bir sarmalayıcı (wrapper) yapıyoruz
    public static class RateLimitedResult {
        public EnhanceCVResponse response;
        public long remainingLimit;
    }

    public RateLimitedResult enhanceCandidateCV() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        // ----- REDIS RATE LIMIT BAŞLANGICI -----
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String rateLimitKey = "ai:limit:" + user.getId() + ":" + today;

        // INCR komutu ile sayacı 1 artırıyoruz
        Long currentRequestCount = redisTemplate.opsForValue().increment(rateLimitKey);

        // Eğer anahtar yeni oluşturulduysa (yani bugün ilk defa istek atılıyorsa) TTL'i gece yarısına kuruyoruz
        if (currentRequestCount != null && currentRequestCount == 1) {
            long secondsUntilMidnight = Duration.between(
                    LocalDateTime.now(),
                    LocalDateTime.now().with(LocalTime.MAX)
            ).getSeconds();
            redisTemplate.expire(rateLimitKey, Duration.ofSeconds(secondsUntilMidnight));
        }

        // Limit aşımı kontrolü (Günde en fazla 10 istek)
        if (currentRequestCount != null && currentRequestCount > 10) {
            // İpucu
            throw new RuntimeException("429 Too Many Requests: Daily AI limit exceeded!");
        }

        long remainingLimit = 10 - (currentRequestCount != null ? currentRequestCount : 0);
        // ----- REDIS RATE LIMIT BİTİŞİ -----

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Candidate profile not found for this user."));

        String enhancedSummary = geminiService.enhanceSummary(
                profile.getSummary(),
                profile.getExperienceYears()
        );

        EnhanceCVResponse response = new EnhanceCVResponse();
        response.setSummary(profile.getSummary());
        response.setEnhancedSummary(enhancedSummary);

        RateLimitedResult result = new RateLimitedResult();
        result.response = response;
        result.remainingLimit = remainingLimit;

        return result;
    }
}