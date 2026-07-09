package com.project.smartmatch.model.request;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for computing AI job matching scores")
public class AIMatchResultRequest {
    @NotNull(message = "Job id cannot be null!")
    @Schema(description = "The database ID of the target job posting", example = "1")
    private Long jobId;

    @NotNull (message = "Candidate id cannot be null!")
    @Schema(description = "The database ID of the candidate profile to match", example = "5")
    private Long candidateId;
}