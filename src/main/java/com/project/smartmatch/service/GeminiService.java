package com.project.smartmatch.service;

import com.project.smartmatch.model.response.AISkillsGapResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // 1. Skill Gap analiz metodun (Buna dokunma, aynen korunsun)
    public AISkillsGapResponse analyzeSkillGap(AISkillsGapService.AiPayload payload) {
        String prompt = String.format(
                "Compare the required skills of the job with the candidate's current skills. " +
                        "Identify missing skills, suggest a detailed learning path for each, and provide an estimated learning time. " +
                        "Required Skills: %s. Candidate Skills: %s. " +
                        "You MUST output your response matching exactly this root object JSON structure: " +
                        "{\"missingSkills\": [{\"skillName\": \"string\", \"learningPathSuggestion\": \"string\", \"estimatedLearningTime\": \"string\"}]}",
                payload.getRequiredSkills(), payload.getCandidateSkills()
        );

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> parts = Map.of("parts", List.of(textPart));
        Map<String, Object> generationConfig = Map.of("responseMimeType", "application/json");
        Map<String, Object> geminiPayload = Map.of("contents", List.of(parts), "generationConfig", generationConfig);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            Map<String, Object> rawResponse = restTemplate.postForObject(url, geminiPayload, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) rawResponse.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> partsList = (List<Map<String, Object>>) content.get("parts");

            String cleanJsonText = ((String) partsList.get(0).get("text")).trim();
            cleanJsonText = cleanJsonText.replace("```json", "").replace("```", "").trim();

            List<Object> skillsList;
            if (cleanJsonText.startsWith("[")) {
                skillsList = objectMapper.readValue(cleanJsonText, List.class);
            } else {
                Map<String, Object> parsedMap = objectMapper.readValue(cleanJsonText, Map.class);
                skillsList = (List<Object>) parsedMap.get("missingSkills");
            }

            AISkillsGapResponse finalResponse = new AISkillsGapResponse();
            finalResponse.setMissingSkills(skillsList);
            return finalResponse;
        } catch (Exception e) {
            throw new RuntimeException("Gemini integration failed: " + e.getMessage(), e);
        }
    }

    // 2. DEĞİŞTİRECEĞİN METOT: Yapay zekaya yılı metne yedirmesini söyleyen prompt burası
    public String enhanceSummary(String originalSummary, Integer experienceYears) {
        // Yapay zekaya tecrübe yılını parametre olarak veriyoruz. Metnin içine kendisi yazacak.
        String prompt = String.format(
                "Rewrite the following candidate profile summary to make it more professional, impactful, and corporate. " +
                        "The candidate has %d years of experience. You MUST  mention this experience level or the specific number of years within the body of the text (if 0 years, describe them as an 'Entry-level' or 'Junior' engineer, Don't forget this detail!). " +
                        "CRITICAL RULE: The final response MUST NOT exceed 200 words. Do not include any explanations, greetings, introduction, markdown styling or extra text. " +
                        "Just return the rewritten profile summary directly in English.\n\n" +
                        "Original Summary: %s",
                experienceYears != null ? experienceYears : 0, originalSummary
        );

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> parts = Map.of("parts", List.of(textPart));
        Map<String, Object> generationConfig = Map.of("responseMimeType", "text/plain");
        Map<String, Object> geminiPayload = Map.of("contents", List.of(parts), "generationConfig", generationConfig);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            Map<String, Object> rawResponse = restTemplate.postForObject(url, geminiPayload, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) rawResponse.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> partsList = (List<Map<String, Object>>) content.get("parts");

            return ((String) partsList.get(0).get("text")).trim();
        } catch (Exception e) {
            throw new RuntimeException("Gemini Enhance CV integration failed: " + e.getMessage(), e);
        }
    }
}