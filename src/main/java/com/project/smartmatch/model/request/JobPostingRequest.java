package com.project.smartmatch.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Job posting creation request model")
public class JobPostingRequest {

    @NotBlank(message = "Job title cannot be blank")
    @Schema(description = "Job title", example = "Senior Java Developer")
    private String title;

    @NotBlank(message = "Job description cannot be blank")
    @Schema(description = "Detailed job description and requirements", example = "We are looking for a backend developer proficient in Spring Boot...")
    private String description;

    @NotEmpty(message = "At least one required skill must be provided")
    @Schema(description = "List of required technical skills", example = "[\"Java\", \"Spring Boot\", \"PostgreSQL\"]")
    private List<String> requiredSkills;

    @NotBlank(message = "City cannot be blank")
    @Schema(description = "Location city", example = "Istanbul")
    private String city;

    @Schema(description = "Minimum budget salary range", example = "45000")
    private Integer salaryMin;

    @Schema(description = "Maximum budget salary range", example = "70000")
    private Integer salaryMax;
}
//Request DTO'su sayesinde kullanıcının sadece title, description, city gibi değiştirmeye izni olan alanları göndermesini zorunlu kılıyoruz.
//@NotBlank(message = "Job title cannot be blank") gibi kuralları buraya yazarak, eksik veya hatalı veri daha Service katmanına bile ulaşmadan API kapısında (Controller'da) isteği reddediyoruz.