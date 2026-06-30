package com.project.smartmatch.controller;

import com.project.smartmatch.model.response.EnhanceCVResponse;
import com.project.smartmatch.service.EnhanceCVService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class EnhanceCVController {

    private final EnhanceCVService enhanceCVService;

    public EnhanceCVController(EnhanceCVService enhanceCVService) {
        this.enhanceCVService = enhanceCVService;
    }

    @PostMapping("/enhance-cv")
    public ResponseEntity<?> enhanceCV() {
        try {
            EnhanceCVService.RateLimitedResult result = enhanceCVService.enhanceCandidateCV();

            // Hocanın istediği X-RateLimit-Remaining header bilgisini buraya ekliyoruz
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