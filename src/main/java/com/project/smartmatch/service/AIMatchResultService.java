package com.project.smartmatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smartmatch.model.entity.AIMatchResult;
import com.project.smartmatch.model.entity.CandidateProfile;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.repository.AIMatchResultRepository;
import com.project.smartmatch.repository.CandidateProfileRepository;
import com.project.smartmatch.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AIMatchResultService {

    private final AIMatchResultRepository aiMatchResultRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    // Spring Bean olarak yönetilmesi daha sağlıklıdır ama hata vermemesi için şimdilik elle init ediyoruz
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // application.yaml dosyasından değerleri çekiyoruz
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public AIMatchResult calculateMatchScore(Long jobId, Long candidateId) throws Exception {
        // 1. Veritabanından İlanı ve Adayı çekiyoruz
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + jobId));

        CandidateProfile candidateProfile = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found with id: " + candidateId));

        // 2. Gemini için Prompt hazırlıyoruz
        String promptText = "You are an AI recruitment assistant. Compare the following job posting and candidate profile.\n\n" +
                "JOB POSTING:\n" +
                "Title: " + jobPosting.getTitle() + "\n" +
                "Description: " + jobPosting.getDescription() + "\n\n" +
                "CANDIDATE PROFILE:\n" +
                "Skills: " + candidateProfile.getSkills() + "\n" +
                "Experience: " + candidateProfile.getExperienceYears() + " years\n\n" +
                "Provide a match score out of 100 and a short reason in English explaining the match.";

        // 3. Gemini API Bilgileri yaml'dan gelen değerlerle birleştiriliyor
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4. Gemini'nin beklediği JSON Gövdesi (Strict JSON Response Ayarlı)
        String jsonRequestBody = "{\n" +
                "  \"contents\": [{\n" +
                "    \"parts\":[{\"text\": \"" + promptText.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]\n" +
                "  }],\n" +
                "  \"generationConfig\": {\n" +
                "    \"responseMimeType\": \"application/json\",\n" +
                "    \"responseSchema\": {\n" +
                "      \"type\": \"OBJECT\",\n" +
                "      \"properties\": {\n" +
                "        \"score\": {\"type\": \"INTEGER\"},\n" +
                "        \"reason\": {\"type\": \"STRING\"}\n" +
                "      },\n" +
                "      \"required\": [\"score\", \"reason\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

        // 5. API Çağrısını gerçekleştiriyoruz
        String aiResponse = restTemplate.postForObject(url, entity, String.class);

        // 6. Gemini'den gelen JSON yanıtını ayıklıyoruz (Parse)
        JsonNode root = objectMapper.readTree(aiResponse);
        String innerJson = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        JsonNode resultJson = objectMapper.readTree(innerJson);
        int score = resultJson.path("score").asInt();
        String reason = resultJson.path("reason").asText();

        // 7. Nesneyi oluşturup veritabanına kaydediyoruz
        AIMatchResult matchResult = new AIMatchResult();
        matchResult.setJobId(jobId);
        matchResult.setCandidateId(candidateId);
        matchResult.setScore(score);
        matchResult.setReason(reason);

        return aiMatchResultRepository.save(matchResult);
    }
}