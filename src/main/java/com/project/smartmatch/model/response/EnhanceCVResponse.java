package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Response data matrix delivery block returning semantic enhancement computation changes")
public class EnhanceCVResponse {
    //   private Long id;
    @Schema(description = "The original text value inputs sent down inside evaluation tracking models before enhancement processing", example = "I am a basic spring boot developer looking for jobs.")
    private String summary;

    @Schema(description = "The advanced optimized copy string rewritten utilizing structural AI parsing pipeline context", example = "Result-driven Software Engineering specialist focusing on backend microservices architecture design configurations utilizing modern Spring Boot ecosystems.") //ai'ın ürettiği özet için
    private String enhancedSummary;
    // private Integer experienceYears;
}