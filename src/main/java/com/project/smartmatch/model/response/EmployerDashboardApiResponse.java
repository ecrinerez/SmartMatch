package com.project.smartmatch.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@Schema(description = "External dashboard summary interface mapping general employer metrics metrics")
public class EmployerDashboardApiResponse {

    @Schema(description = "Total aggregate count of published active job descriptions created by provider", example = "5")
    private Long totalPostings;

    @Schema(description = "Total pending pipeline verification queue metrics remaining open", example = "3")
    private Long pendingApplications;

    @Schema(description = "Total structural counter matching approved processed workflows", example = "4")
    private Long acceptedApplications;

    @Schema(description = "Total structural counter tracking declined applicant files completed", example = "1")
    private Long rejectedApplications;
}