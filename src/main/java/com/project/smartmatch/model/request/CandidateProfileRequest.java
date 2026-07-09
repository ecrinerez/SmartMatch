package com.project.smartmatch.model.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for setting or updating candidate profiles")
public class CandidateProfileRequest {
    @Schema(description = "Contact phone number of the applicant", example = "+905551234567")
    private String phoneNumber;

    @Schema(description = "Professional title/headline", example = "Full Stack Engineer")
    private String title;

    @Schema(description = "Cloud storage link or path to uploaded CV file", example = "https://storage.smartmatch.com/resumes/cv_ecrin.pdf")
    private String resumeUrl;

    @Schema(description = "Array of verified technical competencies", example = "[\"Java\", \"Spring Boot\", \"React\", \"PostgreSQL\"]")
    private List<String> skills;

    @Schema(description = "Short biography or professional career objective summary", example = "Passionate Software Engineering student focusing on scalable backend architectures and AI pipelines.")
    private String summary;

    @Schema(description = "Total number of professional or project experience years", example = "2")
    private Integer experienceYears;

    @Schema(description = "Flexible structured schema holding education milestones", example = "{\"university\": \"Altinbas University\", \"department\": \"Software Engineering\", \"gpa\": 3.75}") // Esnek JSON yapısını doğrudan Map olarak alıyoruz
    private Map<String, Object> education;
}