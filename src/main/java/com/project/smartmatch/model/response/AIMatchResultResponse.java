package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Response model containing AI job matching calculation outputs")
public class AIMatchResultResponse {
    @Schema(description = "The database ID of the processed job posting", example = "1")
    private Long jobId;

    @Schema(description = "The database ID of the evaluated candidate profile", example = "5")
    private Long candidateId;

    @Schema(description = "The overall calculated compatibility score percentage", example = "85")
    private Integer score;

    @Schema(description = "Detailed AI text reasoning justifying the calculated score matrix", example = "The candidate has strong Spring Boot experience matching 90% of backend expectations, but lacks advanced Kubernetes insights.")
    private String reason;
}