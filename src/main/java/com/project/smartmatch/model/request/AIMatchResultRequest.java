package com.project.smartmatch.model.request;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIMatchResultRequest {
    @NotNull(message = "Job id cannot be null!")
    private Long jobId;

    @NotNull (message = "Candidate id cannot be null!")
    private Long candidateId;
}
