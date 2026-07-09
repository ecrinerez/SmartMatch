package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Response matrix structural layer mapping detailed job advertisement info")
public class JobPostingResponse {
    @Schema(description = "Unique identity entry ID value key tracking the job opening", example = "1")
    private Long id;

    @Schema(description = "Job classification framework title mapping", example = "Senior Java Developer")
    private String title;

    @Schema(description = "Detailed position profile prerequisites and team role responsibilities data", example = "We are looking for a backend developer proficient in Spring Boot...")
    private String description;

    @Schema(description = "List matching target skills demanded context requirements", example = "[\"Java\", \"Spring Boot\", \"PostgreSQL\"]")
    private List<String> requiredSkills;

    @Schema(description = "Geographic focus operational placement zone city locator", example = "Istanbul")
    private String city;

    @Schema(description = "Minimum baseline target monetary index range parameter configuration", example = "45000")
    private Integer salaryMin;

    @Schema(description = "Maximum scale bracket threshold projection metric parameter context", example = "70000")
    private Integer salaryMax;

    @Schema(description = "Boolean monitoring state flag mapping marketplace listing coverage validation", example = "true")
    private Boolean isActive;

    @Schema(description = "Database tracking profile identification key belonging to parent provider", example = "2")
    private Long employerId;

    @Schema(description = "Corporate official firm identifier name mapping context", example = "Treasy Finansal Teknolojiler A.S.")
    private String companyName;

    @Schema(description = "Timestamp recording database persistence creation timestamp mapping parameters", example = "2026-07-09T09:30:00")
    private LocalDateTime createdAt;
}
//profilin içinde de şifreler, e-postalar, veritabanı iç ID'leri gibi dış dünyanın görmemesi gereken hassas bilgiler yer alabilir. JobPostingResponse kullanarak bu karmaşık nesneyi ayıklıyor.