package com.project.smartmatch.controller;

import com.project.smartmatch.model.response.EnhanceCVResponse;
import com.project.smartmatch.service.EnhanceCVService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ai")
@Tag(name = "4. AI Matching", description = "Operations handling AI-driven candidate and job compatibility analysis")
public class EnhanceCVController {

    private final EnhanceCVService enhanceCVService;

    public EnhanceCVController(EnhanceCVService enhanceCVService) {
        this.enhanceCVService = enhanceCVService;
    }

    @PostMapping("/enhance-cv")
    @Operation(summary = "Enhance candidate CV via AI", description = "Analyzes and provides optimization recommendations for the logged-in candidate's CV using the Gemini API under strict rate limits.")
    public ResponseEntity<?> enhanceCV() {
        try {
            EnhanceCVService.RateLimitedResult result = enhanceCVService.enhanceCandidateCV();

            //X-RateLimit-Remaining header bilgisi
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-RateLimit-Remaining", String.valueOf(result.remainingLimit));

            return new ResponseEntity<>(result.response, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("429")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}