package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.EmployerProfileRequest;
import com.project.smartmatch.model.response.EmployerProfileResponse;
import com.project.smartmatch.service.EmployerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employer-profiles")
@RequiredArgsConstructor
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;

    @PostMapping
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
    public ResponseEntity<EmployerProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EmployerProfileRequest request) {

        String email = userDetails.getUsername();
        // Servis katmanında updateProfile metodunu çağırıyoruz
        EmployerProfileResponse response = employerProfileService.updateProfile(email, request);
        return ResponseEntity.ok(response);
    }
}