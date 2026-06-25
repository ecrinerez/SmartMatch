package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.CandidateProfileRequest;
import com.project.smartmatch.model.response.CandidateProfileResponse;
import com.project.smartmatch.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/candidate")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @PutMapping
    public ResponseEntity<CandidateProfileResponse> updateProfile(@RequestBody CandidateProfileRequest request) {
        return ResponseEntity.ok(candidateProfileService.updateProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateProfileResponse> getProfileById(@PathVariable Integer id) {
        return ResponseEntity.ok(candidateProfileService.getProfileById(id));
    }
}