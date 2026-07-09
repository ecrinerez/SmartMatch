package com.project.smartmatch.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for submitting a new job application")
public class JobApplicationRequest {
    @NotNull(message = "Job id cannot be null!")
    @Schema(description = "The database ID of the target job posting to apply for", example = "10")
    private Long jobId;

    //Aday sadece hangi işe başvurmak istediğini seçerAday sadece hangi işe başvurmak istediğini seçer. Başkasının id'sini
    //girip başvuru yapamamalı, o yüzden candidateId almıyoruz.

    //Bu request'i aday kullanır, diğerini employer, requestleri ayırma sebebimiz bu.
}