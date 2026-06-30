package com.project.smartmatch.model.response;

import java.util.List;

public class AISkillsGapResponse {

    // Artık Map değil, List olarak tutuyoruz ki Postman'da içi içe görünmesin
    private List<Object> missingSkills;

    public List<Object> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<Object> missingSkills) {
        this.missingSkills = missingSkills;
    }
}