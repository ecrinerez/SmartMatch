package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.JobApplicationRequest;
import com.project.smartmatch.model.request.JobApplicationStatusRequest;
import com.project.smartmatch.model.response.JobApplicationResponse;
import com.project.smartmatch.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    // 1. POST /applications -> Adayın ilana başvurması
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CANDIDATE', 'ROLE_CANDIDATE')")
    public ResponseEntity<JobApplicationResponse> applyToJob(
            @Valid @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        JobApplicationResponse response = jobApplicationService.applyToJob(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. GET /applications/my -> Adayın kendi başvurularını görmesi
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('CANDIDATE', 'ROLE_CANDIDATE')")
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<JobApplicationResponse> response = jobApplicationService.getMyApplications(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // 3. GET /applications/job/{jobId} -> İşverenin ilana gelen başvuruları görmesi
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER')")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByJobId(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<JobApplicationResponse> response = jobApplicationService.getApplicationsByJobId(jobId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // 4. PUT /applications/{id}/status -> İşverenin başvuruyu kabul veya ret etmesi
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER')")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        JobApplicationResponse response = jobApplicationService.updateApplicationStatus(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}