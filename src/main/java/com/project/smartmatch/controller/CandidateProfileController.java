package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.CandidateProfileRequest;
import com.project.smartmatch.model.response.CandidateProfileResponse;
import com.project.smartmatch.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/profile/candidate")
@RequiredArgsConstructor
@Tag(name = "5. Candidate Profiles", description = "Operations managing candidate CV and profile information")
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @PutMapping
    @Operation(summary = "Update candidate profile", description = "Updates profile summaries, key experience milestones, and skills for the candidate.")
    public ResponseEntity<CandidateProfileResponse> updateProfile(@RequestBody CandidateProfileRequest request) {
        return ResponseEntity.ok(candidateProfileService.updateProfile(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get candidate profile by ID", description = "Retrieves the full profile details of a single candidate by their profile ID.")
    public ResponseEntity<CandidateProfileResponse> getProfileById(@Parameter(description = "Unique profile ID of the candidate", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(candidateProfileService.getProfileById(id));
    }
}