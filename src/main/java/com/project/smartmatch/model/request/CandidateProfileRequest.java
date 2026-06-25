package com.project.smartmatch.model.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CandidateProfileRequest {
    private String phoneNumber;
    private String title;
    private String resumeUrl;
    private List<String> skills;
    private String summary;
    private Integer experienceYears;
    private Map<String, Object> education; // Esnek JSON yapısını doğrudan Map olarak alıyoruz
}