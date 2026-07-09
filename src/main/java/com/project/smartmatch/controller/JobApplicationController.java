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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "3. Job Applications", description = "Handles candidate application submissions and employer review processes")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    // Adayın bir iş ilanına başvuru yapmasını sağlar.
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CANDIDATE', 'ROLE_CANDIDATE')") // 🚀 Rol eşleşme hatasını önlemek için yetkilendirme esnetildi.
    @Operation(summary = "Submit a job application", description = "Allows authenticated candidates to apply for a specific job posting.")
    public ResponseEntity<JobApplicationResponse> applyToJob(
            @Valid @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        JobApplicationResponse response = jobApplicationService.applyToJob(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Giriş yapan adayın kendi yaptığı tüm başvuruları listeler.
    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('CANDIDATE', 'ROLE_CANDIDATE')")
    @Operation(summary = "Get candidate applications", description = "Lists all previous job applications submitted by the logged-in candidate.")
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<JobApplicationResponse> response = jobApplicationService.getMyApplications(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // İşverenin kendi ilanına gelen tüm başvuruları görmesini sağlar.
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER')") // 🚀 İşveren rol kontrolü güvenli hale getirildi.
    @Operation(summary = "Get applications for a specific job", description = "Allows the job owner employer to view all candidate applications received for a specific posting.")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByJobId(
            @Parameter(description = "Job Posting ID", example = "1") @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<JobApplicationResponse> response = jobApplicationService.getApplicationsByJobId(jobId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // İşverenin başvuruyu ACCEPTED veya REJECTED olarak güncellemesini sağlar.
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER')")
    @Operation(summary = "Update application status (Accept/Reject)", description = "Allows employers to update application status to ACCEPTED or REJECTED. Triggers instant notification to the candidate via Redis.")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @Parameter(description = "Unique ID of the application record", example = "1") @PathVariable Long id,
            @Valid @RequestBody JobApplicationStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        JobApplicationResponse response = jobApplicationService.updateApplicationStatus(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}