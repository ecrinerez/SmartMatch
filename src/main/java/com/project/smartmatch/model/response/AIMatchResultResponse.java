package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIMatchResultResponse {
    private Long jobId;
    private Long candidateId;
    private Integer score;
    private String reason;
}
