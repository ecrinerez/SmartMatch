package com.project.smartmatch.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CandidateDashboardApiResponse {

    private Long totalApplications;
    private Double averageAiMatchScore;
    private List<String> top3JobPostings;
}