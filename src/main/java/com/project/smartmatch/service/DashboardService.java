package com.project.smartmatch.service;

import com.project.smartmatch.model.response.CandidateDashboardResponse;
import com.project.smartmatch.model.response.EmployerDashboardResponse;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.repository.CandidateStatsRepository;
import com.project.smartmatch.repository.EmployerStatsRepository;
import com.project.smartmatch.repository.AIMatchResultRepository;
import com.project.smartmatch.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployerStatsRepository employerStatsRepository;
    private final CandidateStatsRepository candidateStatsRepository;
    private final AIMatchResultRepository aiMatchResultRepository;
    private final JobPostingRepository jobPostingRepository;

    // 1. İşveren İstatistikleri - 10 Dakika Cache'leniyor
    @Cacheable(value = "employerDashboard", key = "#employerId")
    public EmployerDashboardResponse getEmployerDashboard(Long employerId) {
        List<Object[]> rawList = employerStatsRepository.findRawStatsByEmployerId(employerId).orElse(List.of());
        Object[] row = rawList.isEmpty() ? new Object[]{0L, 0L, 0L, 0L} : rawList.get(0);

        return EmployerDashboardResponse.builder()
                .totalPostings(((Number) row[0]).longValue())
                .pendingApplications(((Number) row[1]).longValue())
                .acceptedApplications(((Number) row[2]).longValue())
                .rejectedApplications(((Number) row[3]).longValue())
                .build();
    }

    // 2. Aday İstatistikleri - 10 Dakika Cache'leniyor
    @Cacheable(value = "candidateDashboard", key = "#candidateId")
    public CandidateDashboardResponse getCandidateDashboard(Long candidateId) {
        Optional<Object[]> result = candidateStatsRepository.findRawStatsByCandidateId(candidateId);
        Object[] row = result.orElse(new Object[]{0L});

        Long totalApplications = ((Number) row[0]).longValue();

        // MÜKERRER KAYIT KORUMASI: Aynı ilana ait mükerrer skorlar varsa, sadece en son (en güncel ID'ye sahip) sonucu filtreler.
        // Hem ortalama hesaplarken hem de listeleme yaparken bu temiz listeyi kullanacağız.
        Collection<com.project.smartmatch.model.entity.AIMatchResult> uniqueLatestResults = aiMatchResultRepository.findAll().stream()
                .filter(resultItem -> resultItem.getCandidateId().equals(candidateId))
                .collect(Collectors.toMap(
                        resultItem -> resultItem.getJobId(), // Key: Job ID
                        resultItem -> resultItem,            // Value: AIMatchResult nesnesi
                        (existing, replacement) -> existing.getId() >= replacement.getId() ? existing : replacement
                        // Çakışma durumunda veritabanı ID'si büyük olanı (yani EN SON eklenen güncel skoru) seçer.
                ))
                .values();

        // B) Gerçek Ortalama AI Skoru Hesaplama (Sadece benzersiz ve en son alınan skorların ortalamasını alır)
        Double averageScore = uniqueLatestResults.stream()
                .mapToDouble(resultItem -> resultItem.getScore())
                .average()
                .orElse(0.0);

        // C) En Yüksek Skorlu 3 İlanı Bulma (En son güncel skorlar arasından en iyi 3 tanesini seçer)
        List<String> top3JobTitles = uniqueLatestResults.stream()
                .sorted((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore())) // Skora göre büyükten küçüğe sırala
                .limit(3) // İlk 3 kaydı al
                .map(resultItem -> {
                    // Veritabanından ilanı çekiyoruz
                    JobPosting job = jobPostingRepository.findById(resultItem.getJobId()).orElse(null);

                    // job.getEmployer() ilişkisini kullanarak şirket ismine ulaşıyoruz
                    if (job != null && job.getEmployer() != null) {
                        String companyName = job.getEmployer().getCompanyName();
                        return job.getTitle() + " (" + companyName + ") (Score: " + resultItem.getScore() + ")";
                    } else if (job != null) {
                        return job.getTitle() + " (Score: " + resultItem.getScore() + ")";
                    }
                    return "Unknown Position";
                })
                .collect(Collectors.toList());

        return CandidateDashboardResponse.builder()
                .totalApplications(totalApplications)
                .averageAiMatchScore(Math.round(averageScore * 100.0) / 100.0) // Virgülden sonra 2 basamak yuvarlar
                .top3JobPostings(top3JobTitles)
                .build();
    }
}