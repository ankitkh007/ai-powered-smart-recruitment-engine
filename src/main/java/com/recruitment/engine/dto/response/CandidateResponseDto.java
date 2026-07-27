package com.recruitment.engine.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CandidateResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String skills;
    private BigDecimal experienceYears;
    private String education;
    private String projects;
    private String certifications;
    private String status;
    private LocalDateTime createdAt;

    public CandidateResponseDto(Long id, String name, String email, String phone, String skills,
            BigDecimal experienceYears, String education, String projects,
            String certifications, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.education = education;
        this.projects = projects;
        this.certifications = certifications;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSkills() {
        return skills;
    }

    public BigDecimal getExperienceYears() {
        return experienceYears;
    }

    public String getEducation() {
        return education;
    }

    public String getProjects() {
        return projects;
    }

    public String getCertifications() {
        return certifications;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}