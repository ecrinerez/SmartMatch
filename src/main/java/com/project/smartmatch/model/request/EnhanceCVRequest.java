package com.project.smartmatch.model.request;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for AI-driven candidate CV enhancement")
public class EnhanceCVRequest {
    @Schema(description = "The current professional summary or biography text to enhance", example = "I am a software student trying to learn spring boot.")
    private String summary;

    @Schema(description = "Total number of years of experience to factor into the enhancement", example = "1")
    private Integer experienceYears;
}