package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.JobPostingRequest;
import com.project.smartmatch.model.response.JobPostingResponse;
import com.project.smartmatch.service.JobPostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "2. Job Postings", description = "Operations managing job posting management, filtering, and smart search")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // Sadece EMPLOYER rolündeki giriş yapmış kullanıcının yeni ilan eklemesini sağlar.
    @PostMapping
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER')") // 🚀 KÖKTEN ÇÖZÜM: CustomUserDetailsService veya Token'dan gelen rolün ön ekli/ön eksiz tüm varyasyonlarını kabul ederek 403 hatasını engeller.
    @Operation(summary = "Create a new job posting", description = "Allows authenticated employers to publish a new active job advertisement.")
    public ResponseEntity<JobPostingResponse> createJobPosting(@Valid @RequestBody JobPostingRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        JobPostingResponse response = jobPostingService.createJobPosting(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // İlanları şehre ve aktifliğe göre süzerek, sayfa sayfa (varsayılan 10'arlı) listeler.
    @GetMapping
    @Operation(summary = "Get filtered job postings", description = "Fetches job postings filtered by city and active status with pagination.")
    public ResponseEntity<Page<JobPostingResponse>> getAllJobPostings(
            @Parameter(description = "Filter by city name", example = "Istanbul") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by active status", example = "true") @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<JobPostingResponse> response = jobPostingService.getAllJobPostings(city, isActive, pageable);
        return ResponseEntity.ok(response);
    }

    // Redis Sorted Set üzerinden en son yayınlanan 20 iş ilanını kronolojik olarak getirir.
    @GetMapping("/latest")
    @Operation(summary = "Get latest 20 job postings", description = "Retrieves the most recent 20 job postings chronologically from Redis Sorted Set.")
    public ResponseEntity<List<Object>> getLatest20Jobs() {
        return ResponseEntity.ok(jobPostingService.getLatest20Jobs());
    }

    // ID'si verilen tek bir ilanın tüm detaylarını herkesin görmesini sağlar.
    @GetMapping("/{id}")
    @Operation(summary = "Get job posting details by ID", description = "Retrieves detailed information of a single job posting by its ID.")
    public ResponseEntity<JobPostingResponse> getJobById(@Parameter(description = "Unique ID of the job posting", example = "1") @PathVariable Long id) {
        JobPostingResponse response = jobPostingService.getJobPostingById(id);
        return ResponseEntity.ok(response);
    }

    // Sadece ilanın sahibi olan işverenin ilanı güncellemesine izin verir.
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER')") // 🚀 Güncelleme işlemi için de yetki kontrolü esnetildi.
    @Operation(summary = "Update an existing job posting", description = "Updates job details. Authorized only for the employer who created the posting.")
    public ResponseEntity<JobPostingResponse> updateJobPosting(@Parameter(description = "Unique ID of the job posting", example = "1") @PathVariable Long id,
                                                               @Valid @RequestBody JobPostingRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        JobPostingResponse response = jobPostingService.updateJobPosting(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // Sadece ilanın sahibi olan işverenin ilanı tamamen silmesini sağlar.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER')") // 🚀 Silme işlemi için de yetki kontrolü esnetildi.
    @Operation(summary = "Delete a job posting", description = "Hard deletes a job posting from the system. Authorized only for the owner employer.")
    public ResponseEntity<Void> deleteJobPosting(@Parameter(description = "Unique ID of the job posting", example = "1") @PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        jobPostingService.deleteJobPosting(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/search")
    @Operation(summary = "Smart full-text search for jobs", description = "Performs an advanced smart text search matching job titles and descriptions.")
    public ResponseEntity<List<JobPostingResponse>> searchJobs(@Parameter(description = "Search query keyword", example = "Java") @RequestParam("q") String query) {
        return ResponseEntity.ok(jobPostingService.searchJobs(query));
    }
}