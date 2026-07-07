package com.project.smartmatch.controller;

import com.project.smartmatch.model.response.CandidateDashboardResponse;
import com.project.smartmatch.model.response.EmployerDashboardResponse;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.repository.UserRepository;
import com.project.smartmatch.service.DashboardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping("/employer")
    public ResponseEntity<EmployerDashboardResponse> getEmployerDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        // Giriş yapmış kullanıcının email adresiyle kullanıcıyı ve bağlı olduğu işveren profilini bulur
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (user.getEmployerProfile() == null) {
            throw new IllegalArgumentException("User does not have an employer profile.");
        }

        EmployerDashboardResponse response = dashboardService.getEmployerDashboard((long) user.getEmployerProfile().getId());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/candidate")
    public ResponseEntity<CandidateDashboardResponse> getCandidateDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        // Giriş yapmış kullanıcının email adresiyle kullanıcıyı ve bağlı olduğu aday profilini bulur
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (user.getCandidateProfile() == null) {
            throw new IllegalArgumentException("User does not have a candidate profile.");
        }

        CandidateDashboardResponse response = dashboardService.getCandidateDashboard((long) user.getCandidateProfile().getId());
        return ResponseEntity.ok(response);
    }
}