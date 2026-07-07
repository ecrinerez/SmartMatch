package com.project.smartmatch.model.response;

import com.project.smartmatch.model.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobApplicationResponse {
    private Long id;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
//Adayın hangi ilana başvurduğunu (İlan başlığı, şirket adı vb.) veya
// işveren hangi adayın başvurduğunu (Adayın unvanı, e-postası vb.) görebilmek için eklendi.
    private Long jobId;
    private String jobTitle;
    private String companyName;

    private Long candidateId;
    private String candidateEmail;
    private String candidateTitle;


}
