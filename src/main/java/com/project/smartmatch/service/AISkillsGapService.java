package com.project.smartmatch.service;

import com.project.smartmatch.model.entity.AISkillsGap;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.model.entity.CandidateProfile;
import com.project.smartmatch.model.response.AISkillsGapResponse;
import com.project.smartmatch.repository.AISkillsGapRepository;
import com.project.smartmatch.repository.JobPostingRepository;
import com.project.smartmatch.repository.CandidateProfileRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AISkillsGapService {

    private final AISkillsGapRepository aiSkillsGapRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final GeminiService geminiService;
    private final RedisTemplate<String, Object> redisTemplate; // Redis entegrasyonu eklendi

    public AISkillsGapService(AISkillsGapRepository aiSkillsGapRepository,
                              JobPostingRepository jobPostingRepository,
                              CandidateProfileRepository candidateProfileRepository,
                              GeminiService geminiService,
                              RedisTemplate<String, Object> redisTemplate) { // Constructor'a eklendi
        this.aiSkillsGapRepository = aiSkillsGapRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.geminiService = geminiService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public AISkillsGapResponse getOrCreateSkillsGap(Long jobId, Long candidateId) {
        // ----- REDIS CACHE KONTROLÜ -----
        String cacheKey = "ai:match:" + jobId + ":" + candidateId;

        // Önce Redis'e bakılır, eğer analiz sonucu varsa veritabanına ve Gemini'a hiç gitmeden doğrudan dönülür.
        AISkillsGapResponse cachedResponse = (AISkillsGapResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResponse != null) {
            System.out.println("System: Result retrieved from Redis cache.");
            return cachedResponse;
        }

        // 1. Veritabanı Kontrolü (Eğer Redis'te analiz sonucu yoksa veritabanına bakılır)
        Optional<AISkillsGap> existingGap = aiSkillsGapRepository.findByJobIdAndCandidateId(jobId, candidateId);

        if (existingGap.isPresent()) {
            AISkillsGapResponse response = new AISkillsGapResponse();

            @SuppressWarnings("unchecked")
            Map<String, Object> dbSkills = (Map<String, Object>) existingGap.get().getMissingSkills();

            if (dbSkills != null && dbSkills.containsKey("missingSkills")) {
                @SuppressWarnings("unchecked")
                List<Object> extractedList = (List<Object>) dbSkills.get("missingSkills");
                response.setMissingSkills(extractedList);
            } else {
                response.setMissingSkills(List.of());
            }

            // Veritabanında bulunan bu sonuç da sonraki istekler hızlansın diye Redis'e 24 saatliğine atılır.
            redisTemplate.opsForValue().set(cacheKey, response, 24, TimeUnit.HOURS);
            return response;
        }

        // 2. Gerekli yetenekleri ve aday yeteneklerini veritabanından çekme
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        CandidateProfile candidateProfile = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        // 3. Yapay Zeka Veri Yükünü (Payload) Hazırlama
        AiPayload aiPayload = new AiPayload(jobPosting.getRequiredSkills(), candidateProfile.getSkills());

        // 4. Gemini Servisini Çağırma
        AISkillsGapResponse aiResponse = geminiService.analyzeSkillGap(aiPayload);

        if (aiResponse == null || aiResponse.getMissingSkills() == null) {
            throw new RuntimeException("AI service failed to return a valid analysis");
        }

        // 5. Yapılandırılmış sonucu AISkillsGap tablosuna kaydetme
        AISkillsGap newSkillsGap = new AISkillsGap();
        newSkillsGap.setJobId(jobId);
        newSkillsGap.setCandidateId(candidateId);

        newSkillsGap.setMissingSkills(Map.of("missingSkills", aiResponse.getMissingSkills()));
        aiSkillsGapRepository.save(newSkillsGap);

        // ----- TAZE SONUCU REDIS'E KAYDETME (TTL = 24 SAAT) -----
        redisTemplate.opsForValue().set(cacheKey, aiResponse, 24, TimeUnit.HOURS);
        // --------------------------------------------------------

        return aiResponse;
    }

    // Yapay zeka istek sözleşmesine özel olarak uyarlanmış Saf Veri Transfer Nesnesi (DTO)
    public static class AiPayload {
        private List<String> requiredSkills;
        private List<String> candidateSkills;

        public AiPayload(List<String> requiredSkills, List<String> candidateSkills) {
            this.requiredSkills = requiredSkills;
            this.candidateSkills = candidateSkills;
        }

        public List<String> getRequiredSkills() { return requiredSkills; }
        public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
        public List<String> getCandidateSkills() { return candidateSkills; }
        public void setCandidateSkills(List<String> candidateSkills) { this.candidateSkills = candidateSkills; }
    }
}