package com.project.smartmatch.model.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
@Schema(description = "Cache compatible response map framework tracking provider stats pipeline values")
public class EmployerDashboardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Total aggregate count of published active job descriptions created by provider", example = "5")
    private Long totalPostings;

    @Schema(description = "Total pending pipeline verification queue metrics remaining open", example = "3")
    private Long pendingApplications;

    @Schema(description = "Total structural counter matching approved processed workflows", example = "4")
    private Long acceptedApplications;

    @Schema(description = "Total structural counter tracking declined applicant files completed", example = "1")
    private Long rejectedApplications;
}