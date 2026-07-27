package com.recruitment.engine.service.ai;

public record AIScoreResult(
        Double matchPercentage,
        String confidence,
        String matchingSkills,
        String missingSkills,
        String strengths,
        String weaknesses,
        String summary) {
}
