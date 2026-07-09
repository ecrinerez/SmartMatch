package com.project.smartmatch.model.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
// Bu anotasyon Redis'e nesneyi kaydederken asıl sınıf türünü de eklemesini söyler.
// Böylece merkezi konfigürasyonlarla çakışmadan LinkedHashMap hatasını çözer.
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@Schema(description = "Serializable cache-friendly internal response framework mapping candidate stats")
public class CandidateDashboardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Total number of job applications submitted by the candidate", example = "12")
    private Long totalApplications;

    @Schema(description = "Calculated historic aggregate average AI compatibility rating", example = "78.4")
    private Double averageAiMatchScore;

    @Schema(description = "Titles of the top 3 high-matching job posting options recommendations", example = "[\"Senior Java Engineer\", \"Backend Intern\", \"Cloud Developer\"]")
    private List<String> top3JobPostings; // En yüksek skorlu 3 ilanın başlığı veya detay listesi
}