package com.project.smartmatch.controller;

import com.project.smartmatch.model.entity.AIMatchResult;
import com.project.smartmatch.model.request.AIMatchResultRequest;
import com.project.smartmatch.model.response.AIMatchResultResponse;
import com.project.smartmatch.service.AIMatchResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "4. AI Matching", description = "Operations handling AI-driven candidate and job compatibility analysis")
public class AIMatchResultController {
    private final AIMatchResultService aiMatchResultService;
    @PostMapping("/match")
    @Operation(summary = "Calculate AI match score", description = "Processes a candidate profile against a job posting via Gemini API to evaluate matching score and reasoning.")
    public ResponseEntity<AIMatchResultResponse> match(@Valid @RequestBody AIMatchResultRequest request) throws Exception {
        AIMatchResult entityResult=aiMatchResultService.calculateMatchScore (
                request.getJobId(),
                request.getCandidateId()

        );
        AIMatchResultResponse response= new AIMatchResultResponse();
        response.setJobId(entityResult.getJobId());
        response.setCandidateId(entityResult.getCandidateId());
        response.setScore(entityResult.getScore());
        response.setReason(entityResult.getReason());

        return ResponseEntity.ok(response);
    }

}