package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployerProfileResponse {
    private Long id;
    private UserSimpleResponse user;
    private String companyName;
    private String industry;
    private String websiteUrl;
    private String phoneNumber;
    private String description;
}