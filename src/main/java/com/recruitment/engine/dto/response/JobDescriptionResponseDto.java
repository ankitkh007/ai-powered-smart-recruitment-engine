package com.recruitment.engine.dto.response;

import java.time.LocalDateTime;

public class JobDescriptionResponseDto {

    private Long id;
    private String title;
    private String description;
    private String requiredSkills;
    private String status;
    private String createdByEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public JobDescriptionResponseDto(Long id, String title, String description, String requiredSkills,
            String status, String createdByEmail,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.status = status;
        this.createdByEmail = createdByEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}