package com.project.smartmatch.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobPostingRequest {

    @NotBlank(message = "Job title cannot be blank")
    private String title;

    @NotBlank(message = "Job description cannot be blank")
    private String description;

    @NotEmpty(message = "At least one required skill must be provided")
    private List<String> requiredSkills;

    @NotBlank(message = "City cannot be blank")
    private String city;

    private Integer salaryMin;
    private Integer salaryMax;
}
//Request DTO'su sayesinde kullanıcının sadece title, description, city gibi değiştirmeye izni olan alanları göndermesini zorunlu kılıyoruz.
//@NotBlank(message = "Job title cannot be blank") gibi kuralları buraya yazarak, eksik veya hatalı veri daha Service katmanına bile ulaşmadan API kapısında (Controller'da) isteği reddediyoruz.