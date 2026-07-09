package com.project.smartmatch.model.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model displaying missing skills data analysis breakdown")
public class AISkillsGapResponse {

    // Artık Map değil, List olarak tutuyoruz ki Postman'da içi içe görünmesin
    @Schema(description = "Array containing structured missing skill items and recommendations", example = "[\"Docker\", \"AWS\", \"Microservices Architecture\"]")
    private List<Object> missingSkills;

    public List<Object> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<Object> missingSkills) {
        this.missingSkills = missingSkills;
    }
}