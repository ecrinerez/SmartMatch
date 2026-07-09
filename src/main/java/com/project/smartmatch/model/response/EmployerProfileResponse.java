package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Response data block structure holding full information regarding corporate vendor profile data")
public class EmployerProfileResponse {
    @Schema(description = "Unique key database record identity targeting firm profile tracking instance", example = "1")
    private Long id;

    @Schema(description = "Simplified parent user profile data encapsulation nested block context")
    private UserSimpleResponse user;

    @Schema(description = "Registered trade corporate entity operational naming assignment label", example = "Treasy Finansal Teknolojiler A.S.")
    private String companyName;

    @Schema(description = "Commercial industry operations structural market vertical label context", example = "Fintech / Software Engineering")
    private String industry;

    @Schema(description = "Public online reference internet domain location target network address link", example = "https://treasy.com")
    private String websiteUrl;

    @Schema(description = "Corporate communication help desk contact connection string line identification", example = "+902125554433")
    private String phoneNumber;

    @Schema(description = "Detailed organization culture overview structural mission explanation statement block text", example = "Leading fintech platform group building payment microservices frameworks.")
    private String description;
}