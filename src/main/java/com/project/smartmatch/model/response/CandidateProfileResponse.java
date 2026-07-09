package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Response model showing detailed candidate profile information")
public class CandidateProfileResponse {
    @Schema(description = "Unique database ID of the candidate profile", example = "1")
    private Long id;

    @Schema(description = "Associated base account user ID", example = "4")
    private Long userId;

    @Schema(description = "Primary email address of the applicant account", example = "candidate1@gmail.com")
    private String email;

    @Schema(description = "Contact phone line identifier", example = "+905551234567")
    private String phoneNumber;

    @Schema(description = "Professional title header placement", example = "Full Stack Engineer")
    private String title;

    @Schema(description = "Cloud repository reference locator pointing to uploaded CV file", example = "https://storage.smartmatch.com/resumes/cv_ecrin.pdf")
    private String resumeUrl;

    @Schema(description = "Array containing technical core competencies registered", example = "[\"Java\", \"Spring Boot\", \"React\"]")
    private List<String> skills;

    @Schema(description = "Personal career objective summary profile text block", example = "Passionate Software Engineering student focusing on scalable backend architectures.")
    private String summary;

    @Schema(description = "Total quantitative summary of career experience tracking milestones", example = "2")
    private Integer experienceYears;

    @Schema(description = "Flexible structural schema map tracking educational timeline data entries", example = "{\"university\": \"Altinbas University\", \"department\": \"Software Engineering\"}")
    private Map<String, Object> education;
}