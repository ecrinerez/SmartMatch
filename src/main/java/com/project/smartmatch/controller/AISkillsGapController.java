package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.AISkillsGapRequest;
import com.project.smartmatch.model.response.AISkillsGapResponse;
import com.project.smartmatch.service.AISkillsGapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AISkillsGapController {

    private final AISkillsGapService aiSkillsGapService;

    public AISkillsGapController(AISkillsGapService aiSkillsGapService) {
        this.aiSkillsGapService = aiSkillsGapService;
    }

    @GetMapping("/skill-gap")
    public ResponseEntity<AISkillsGapResponse> getSkillGap(@Valid @ModelAttribute AISkillsGapRequest request) {
        // @ModelAttribute, GET isteğindeki Query Parametrelerini (?jobId=X&candidateId=Y) otomatik olarak nesneye bağlar.
        // @Valid ise request sınıfındaki @NotNull anotasyonlarını kontrol eder.

        AISkillsGapResponse response = aiSkillsGapService.getOrCreateSkillsGap(
                request.getJobId(),
                request.getCandidateId()
        );

        return ResponseEntity.ok(response);
    }
}