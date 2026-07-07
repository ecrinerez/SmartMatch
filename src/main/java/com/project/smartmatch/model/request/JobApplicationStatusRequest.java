package com.project.smartmatch.model.request;

import com.project.smartmatch.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationStatusRequest {

    @NotNull(message = "Status cannot be null!")
    private ApplicationStatus status;
}
 //Bu kısım employer'ı ilgilendirir. ACCEPTED vb. yazar.