package com.project.smartmatch.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')") // Sadece ROLE_CANDIDATE yetkisi olanlar erişebilir.
    public ResponseEntity<String> applyToJob(@RequestBody String applicationDetails) {
        return ResponseEntity.ok("Application received successfully (Candidate Access Approved)).");
    }
}