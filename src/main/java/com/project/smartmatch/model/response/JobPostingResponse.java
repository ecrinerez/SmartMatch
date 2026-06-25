package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobPostingResponse {
    private Long id;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private String city;
    private Integer salaryMin;
    private Integer salaryMax;
    private Boolean isActive;
    private Long employerId;
    private String companyName;
    private LocalDateTime createdAt;
}
//profilin içinde de şifreler, e-postalar, veritabanı iç ID'leri gibi dış dünyanın görmemesi gereken hassas bilgiler yer alabilir. JobPostingResponse kullanarak bu karmaşık nesneyi ayıklıyor.