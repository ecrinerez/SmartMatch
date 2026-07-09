package com.project.smartmatch.model.response;

import com.project.smartmatch.model.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Response model showing full job application tracking data")
public class JobApplicationResponse {
    @Schema(description = "Unique application record database identity", example = "1")
    private Long id;

    @Schema(description = "Current operational status decision workflow status", example = "PENDING", allowableValues = {"PENDING", "ACCEPTED", "REJECTED"})
    private ApplicationStatus status;

    @Schema(description = "Timestamp when the application transaction was initialized", example = "2026-07-09T10:00:00")
    private LocalDateTime appliedAt;

    @Schema(description = "Timestamp tracking the most recent review state update event", example = "2026-07-09T11:15:00")
    private LocalDateTime updatedAt;
    //Adayın hangi ilana başvurduğunu (İlan başlığı, şirket adı vb.) veya
// işveren hangi adayın başvurduğunu (Adayın unvanı, e-postası vb.) görebilmek için eklendi.
    @Schema(description = "Database ID of the associated target job opening", example = "10")
    private Long jobId;

    @Schema(description = "Official job opening recruitment title header text", example = "Senior Java Developer")
    private String jobTitle;

    @Schema(description = "Trade corporate business provider entity identifier name", example = "Treasy Finansal Teknolojiler A.S.")
    private String companyName;

    @Schema(description = "Database profile ID reference belonging to the candidate applicant", example = "5")
    private Long candidateId;

    @Schema(description = "Primary user login identity email address reference", example = "candidate1@gmail.com")
    private String candidateEmail;

    @Schema(description = "Professional profile title context listed by applicant", example = "Full Stack Engineer")
    private String candidateTitle;


}