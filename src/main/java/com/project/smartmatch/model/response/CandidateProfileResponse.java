package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CandidateProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String phoneNumber;
    private String title;
    private String resumeUrl;
    private List<String> skills;
    private String summary;
    private Integer experienceYears;
    private Map<String, Object> education;
}