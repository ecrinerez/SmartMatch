package com.project.smartmatch.service;

import com.project.smartmatch.model.request.CandidateProfileRequest;
import com.project.smartmatch.model.response.CandidateProfileResponse;
import com.project.smartmatch.model.entity.CandidateProfile;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.repository.CandidateProfileRepository;
import com.project.smartmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public CandidateProfileResponse updateProfile(CandidateProfileRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    CandidateProfile newProfile = new CandidateProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setTitle(request.getTitle());
        profile.setResumeUrl(request.getResumeUrl()); // pdf vb olarak almak veritabanını çok şişirir, performansı düşürür.
        profile.setSkills(request.getSkills()); // Adayın yeteneklerini PostgreSQL'de text[] (array) olarak saklamak için List<String> kullanıldı.
        profile.setSummary(request.getSummary());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setEducation(request.getEducation()); // jsonb yapısı

        CandidateProfile updatedProfile = candidateProfileRepository.save(profile);
        return convertToResponse(updatedProfile);
    }

    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfileById(Integer id) {
        CandidateProfile profile = candidateProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with ID: " + id));

        return convertToResponse(profile);
    }

    private CandidateProfileResponse convertToResponse(CandidateProfile profile) {
        CandidateProfileResponse response = new CandidateProfileResponse();
        response.setId(profile.getId()); // id sırası tutar; 1,2,3..
        response.setUserId(profile.getUser().getId()); // unique= 1 sıra numasının(id'sinin) sadece bir kişide olduğundan emin olur.
        response.setEmail(profile.getUser().getEmail());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setTitle(profile.getTitle());
        response.setResumeUrl(profile.getResumeUrl());
        response.setSkills(profile.getSkills());
        response.setSummary(profile.getSummary());
        response.setExperienceYears(profile.getExperienceYears());
        response.setEducation(profile.getEducation());
        return response;
    }
}