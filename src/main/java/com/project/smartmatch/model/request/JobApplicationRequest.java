package com.project.smartmatch.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationRequest {
        @NotNull(message = "Job id cannot be null!")
        private Long jobId;

       //Aday sadece hangi işe başvurmak istediğini seçerAday sadece hangi işe başvurmak istediğini seçer. Başkasının id'sini
    //girip başvuru yapamamalı, o yüzden candidateId almıyoruz.

    //Bu request'i aday kullanır, diğerini employer, requestleri ayırma sebebimiz bu.
    }

