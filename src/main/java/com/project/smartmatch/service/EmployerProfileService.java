package com.project.smartmatch.service;

import com.project.smartmatch.model.request.EmployerProfileRequest;
import com.project.smartmatch.model.response.EmployerProfileResponse;
import com.project.smartmatch.model.response.UserSimpleResponse;
import com.project.smartmatch.model.entity.EmployerProfile;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.repository.EmployerProfileRepository;
import com.project.smartmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployerProfileService {

    private final EmployerProfileRepository employerProfileRepository;
    private final UserRepository userRepository;

    public EmployerProfileResponse createProfile(String email, EmployerProfileRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if the user already has a profile
        employerProfileRepository.findByUser(user).ifPresent(p -> {
            throw new RuntimeException("This user already has an employer profile");
        });

        // 3. Map Request DTO to Entity
        EmployerProfile profile = new EmployerProfile();
        profile.setUser(user);
        profile.setCompanyName(request.getCompanyName());
        profile.setIndustry(request.getIndustry());
        profile.setWebsiteUrl(request.getWebsiteUrl());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setDescription(request.getDescription());

        // 4. Save to database
        EmployerProfile savedProfile = employerProfileRepository.save(profile);

        // 5. Convert to Response DTO and return
        return convertToResponse(savedProfile);
    }

    // ADDED: Method to update an existing profile instead of trying to create a new one
    @Transactional
    public EmployerProfileResponse updateProfile(String email, EmployerProfileRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Find the existing profile linked to this user
        EmployerProfile profile = employerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employer profile not found"));

        // 3. Update fields with the new request data
        profile.setCompanyName(request.getCompanyName());
        profile.setIndustry(request.getIndustry());
        profile.setWebsiteUrl(request.getWebsiteUrl());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setDescription(request.getDescription());

        // 4. Save updated profile and convert to response DTO
        EmployerProfile updatedProfile = employerProfileRepository.save(profile);
        return convertToResponse(updatedProfile);
    }

    private EmployerProfileResponse convertToResponse(EmployerProfile profile) {
        EmployerProfileResponse response = new EmployerProfileResponse();
        response.setId(profile.getId());
        response.setCompanyName(profile.getCompanyName());
        response.setIndustry(profile.getIndustry());
        response.setWebsiteUrl(profile.getWebsiteUrl());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setDescription(profile.getDescription());

        UserSimpleResponse userSimple = new UserSimpleResponse();
        userSimple.setId(profile.getUser().getId());
        userSimple.setEmail(profile.getUser().getEmail());

        response.setUser(userSimple);
        return response;
    }
}