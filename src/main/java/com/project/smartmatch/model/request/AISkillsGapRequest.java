package com.project.smartmatch.model.request;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for analyzing missing skills via AI")
public class AISkillsGapRequest {
    @NotNull(message = "Job id cannot be null!")
    @Schema(description = "The database ID of the job posting to analyze against", example = "2")
    private Long jobId;

    @NotNull (message = "Candidate id cannot be null!")
    @Schema(description = "The database ID of the candidate being assessed", example = "3")
    private Long candidateId;
}