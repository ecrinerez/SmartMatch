package com.project.smartmatch.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@Schema(description = "External API response model summarizing candidate metric dashboards")
public class CandidateDashboardApiResponse {

    @Schema(description = "Total number of job applications submitted by the candidate", example = "12")
    private Long totalApplications;

    @Schema(description = "Calculated historic aggregate average AI compatibility rating", example = "78.4")
    private Double averageAiMatchScore;

    @Schema(description = "Titles of the top 3 high-matching job posting options recommendations", example = "[\"Senior Java Engineer\", \"Backend Intern\", \"Cloud Developer\"]")
    private List<String> top3JobPostings;
}