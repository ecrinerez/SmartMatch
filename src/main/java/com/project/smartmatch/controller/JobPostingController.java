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

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // Sadece EMPLOYER rolündeki giriş yapmış kullanıcının yeni ilan eklemesini sağlar.
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobPostingResponse> createJobPosting(@Valid @RequestBody JobPostingRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        JobPostingResponse response = jobPostingService.createJobPosting(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // İlanları şehre ve aktifliğe göre süzerek, sayfa sayfa (varsayılan 10'arlı) listeler.
    @GetMapping
    public ResponseEntity<Page<JobPostingResponse>> getAllJobPostings(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<JobPostingResponse> response = jobPostingService.getAllJobPostings(city, isActive, pageable);
        return ResponseEntity.ok(response);
    }

    // Redis Sorted Set üzerinden en son yayınlanan 20 iş ilanını kronolojik olarak getirir.
    @GetMapping("/latest")
    public ResponseEntity<List<Object>> getLatest20Jobs() {
        return ResponseEntity.ok(jobPostingService.getLatest20Jobs());
    }

    // ID'si verilen tek bir ilanın tüm detaylarını herkesin görmesini sağlar.
    @GetMapping("/{id}")
    public ResponseEntity<JobPostingResponse> getJobById(@PathVariable Long id) {
        JobPostingResponse response = jobPostingService.getJobPostingById(id);
        return ResponseEntity.ok(response);
    }

    // Sadece ilanın sahibi olan işverenin ilanı güncellemesine izin verir.
    @PutMapping("/{id}")
    public ResponseEntity<JobPostingResponse> updateJobPosting(@PathVariable Long id,
                                                               @Valid @RequestBody JobPostingRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        JobPostingResponse response = jobPostingService.updateJobPosting(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // Sadece ilanın sahibi olan işverenin ilanı tamamen silmesini sağlar.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosting(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        jobPostingService.deleteJobPosting(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/jobs/search")
    public ResponseEntity<List<JobPostingResponse>> searchJobs(@RequestParam("q") String query) {
        return ResponseEntity.ok(jobPostingService.searchJobs(query));
    }
}