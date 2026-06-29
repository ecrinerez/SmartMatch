package com.project.smartmatch.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployerProfileRequest {
    private String companyName;
    private String description;
    private String industry;
    private String phoneNumber;
    private String websiteUrl;

}