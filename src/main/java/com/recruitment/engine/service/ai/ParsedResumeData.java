package com.recruitment.engine.service.ai;

public record ParsedResumeData(
        String skills,
        Double experienceYears,
        String education,
        String projects,
        String certifications) {
}