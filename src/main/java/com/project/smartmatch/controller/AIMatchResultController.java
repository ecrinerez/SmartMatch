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

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor

public class AIMatchResultController {
    private final AIMatchResultService aiMatchResultService;
    @PostMapping("/match")
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
