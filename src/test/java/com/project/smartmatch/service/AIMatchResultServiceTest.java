package com.project.smartmatch.service;

import com.project.smartmatch.model.entity.AIMatchResult;
import com.project.smartmatch.model.entity.CandidateProfile;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.repository.AIMatchResultRepository;
import com.project.smartmatch.repository.CandidateProfileRepository;
import com.project.smartmatch.repository.JobPostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIMatchResultServiceTest {

    @Mock
    private AIMatchResultRepository aiMatchResultRepository;
    @Mock
    private JobPostingRepository jobPostingRepository;
    @Mock
    private CandidateProfileRepository candidateProfileRepository;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AIMatchResultService aiMatchResultService;

    private JobPosting sampleJob;
    private CandidateProfile sampleCandidate;
    private AIMatchResult sampleResult;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiMatchResultService, "geminiApiUrl", "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent");
        ReflectionTestUtils.setField(aiMatchResultService, "geminiApiKey", "mock-api-key");
        ReflectionTestUtils.setField(aiMatchResultService, "restTemplate", restTemplate);

        sampleJob = new JobPosting();
        sampleJob.setId(1L);
        sampleJob.setTitle("Backend Engineer");
        sampleJob.setDescription("Spring Boot and Redis experience required.");

        sampleCandidate = new CandidateProfile();
        sampleCandidate.setId(2L);
        sampleCandidate.setSkills(List.of("Java", "Spring Boot"));
        sampleCandidate.setExperienceYears(3);

        sampleResult = new AIMatchResult();
        sampleResult.setId(10L);
        sampleResult.setJobId(1L);
        sampleResult.setCandidateId(2L);
        sampleResult.setScore(85);
        sampleResult.setReason("Good framework alignment.");
    }

    @Test
    @DisplayName("Should successfully parse strict JSON model from Gemini and persist AIMatchResult")
    void calculateMatchScore_Success() throws Exception {
        String mockGeminiResponse = "{\n" +
                "  \"candidates\": [{\n" +
                "    \"content\": {\n" +
                "      \"parts\": [{\n" +
                "        \"text\": \"{\\\"score\\\": 85, \\\"reason\\\": \\\"Good framework alignment.\\\"}\"\n" +
                "      }]\n" +
                "    }\n" +
                "  }]\n" +
                "}";

        when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(candidateProfileRepository.findById(2L)).thenReturn(Optional.of(sampleCandidate));
        when(restTemplate.postForObject(anyString(), any(), eq(String.class))).thenReturn(mockGeminiResponse);
        when(aiMatchResultRepository.save(any(AIMatchResult.class))).thenReturn(sampleResult);

        AIMatchResult result = aiMatchResultService.calculateMatchScore(1L, 2L);

        assertNotNull(result);
        assertEquals(85, result.getScore());
        assertEquals("Good framework alignment.", result.getReason());
        verify(aiMatchResultRepository, times(1)).save(any(AIMatchResult.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when requested job posting inside calculation is not found")
    void calculateMatchScore_JobNotFound_ThrowsException() {
        when(jobPostingRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                aiMatchResultService.calculateMatchScore(1L, 2L)
        );

        assertTrue(exception.getMessage().contains("Job posting not found"));
        verifyNoInteractions(candidateProfileRepository, restTemplate, aiMatchResultRepository);
    }

    @Test
    @DisplayName("Should throw RuntimeException when candidate profile inside calculation is not found")
    void calculateMatchScore_CandidateNotFound_ThrowsException() {
        when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(candidateProfileRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                aiMatchResultService.calculateMatchScore(1L, 2L)
        );

        assertTrue(exception.getMessage().contains("Candidate profile not found"));
        verifyNoInteractions(restTemplate, aiMatchResultRepository);
    }
}