package com.project.smartmatch.model.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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
public class CandidateDashboardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalApplications;
    private Double averageAiMatchScore;
    private List<String> top3JobPostings; // En yüksek skorlu 3 ilanın başlığı veya detay listesi
}