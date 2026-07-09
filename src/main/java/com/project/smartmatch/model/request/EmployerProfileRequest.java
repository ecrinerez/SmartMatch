package com.project.smartmatch.model.request;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for creating or modifying corporate provider settings")
public class EmployerProfileRequest {
    @Schema(description = "Official trade or corporate registration name", example = "Treasy Finansal Teknolojiler A.S.")
    private String companyName;

    @Schema(description = "Corporate mission statement, culture blueprint or organization details", example = "Leading fintech group building modern digital payment gateways and robust accounting tools.")
    private String description;

    @Schema(description = "Primary industrial taxonomy domain classification", example = "Fintech / Software Development")
    private String industry;

    @Schema(description = "Primary corporate helpdesk phone line", example = "+902125554433")
    private String phoneNumber;

    @Schema(description = "Public landing network domain locator endpoint", example = "https://treasy.com")
    private String websiteUrl;
}