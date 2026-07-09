package com.project.smartmatch.model.request;

import com.project.smartmatch.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for changing the status of a job application")
public class JobApplicationStatusRequest {

    @NotNull(message = "Status cannot be null!")
    @Schema(description = "The new review status target decision", example = "ACCEPTED", allowableValues = {"PENDING", "ACCEPTED", "REJECTED"})
    private ApplicationStatus status;
}
//Bu kısım employer'ı ilgilendirir. ACCEPTED vb. yazar.