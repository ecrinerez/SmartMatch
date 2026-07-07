package com.project.smartmatch.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmployerDashboardApiResponse {

    private Long totalPostings;
    private Long pendingApplications;
    private Long acceptedApplications;
    private Long rejectedApplications;
}