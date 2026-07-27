package com.recruitment.engine.dto.response;

public class AIScoreResponseDto {
    private Double matchPercentage;
    private String confidence;
    private String matchingSkills;
    private String missingSkills;
    private String strengths;
    private String weaknesses;
    private String summary;

    public AIScoreResponseDto(Double matchPercentage, String confidence, String matchingSkills,
            String missingSkills, String strengths, String weaknesses, String summary) {
        this.matchPercentage = matchPercentage;
        this.confidence = confidence;
        this.matchingSkills = matchingSkills;
        this.missingSkills = missingSkills;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.summary = summary;
    }

    public Double getMatchPercentage() {
        return matchPercentage;
    }

    public String getConfidence() {
        return confidence;
    }

    public String getMatchingSkills() {
        return matchingSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public String getStrengths() {
        return strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public String getSummary() {
        return summary;
    }
}