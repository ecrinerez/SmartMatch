package com.project.smartmatch.model.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
public class EmployerDashboardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalPostings;
    private Long pendingApplications;
    private Long acceptedApplications;
    private Long rejectedApplications;
}