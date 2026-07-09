package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.EmployerProfileRequest;
import com.project.smartmatch.model.response.EmployerProfileResponse;
import com.project.smartmatch.service.EmployerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/employer-profiles")
@RequiredArgsConstructor
@Tag(name = "7. Employer Profiles", description = "Operations managing corporate company identity profiles")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;

    @PostMapping
    @Operation(summary = "Create an employer profile", description = "Initializes corporate business details, website URLs, and industry domains for an employer account.")
    public ResponseEntity<EmployerProfileResponse> createProfile(
            @AuthenticationPrincipal UserDetails userDetails, // Filtrenin yakaladığı kullanıcıyı direkt alır
            @RequestBody EmployerProfileRequest request) {

        // CustomUserDetailsService içindeki loadUserByUsername muhtemelen email döndürüyor
        String email = userDetails.getUsername();

        // Service katmanına işi devrediyoruz
        EmployerProfileResponse response = employerProfileService.createProfile(email, request);

        return ResponseEntity.ok(response);
    }
    @PutMapping
    @Operation(summary = "Update an employer profile", description = "Modifies existing company definitions, descriptions, or updated phone numbers.")
    public ResponseEntity<EmployerProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EmployerProfileRequest request) {

        String email = userDetails.getUsername();
        // Servis katmanında updateProfile metodunu çağırıyoruz
        EmployerProfileResponse response = employerProfileService.updateProfile(email, request);
        return ResponseEntity.ok(response);
    }
}