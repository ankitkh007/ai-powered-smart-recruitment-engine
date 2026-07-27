package com.recruitment.engine.service.ai;

public interface AIScoringService {
    AIScoreResult scoreCandidate(String candidateProfile, String jobDescriptionText);
}