package com.recruitment.engine.dto.request;

import jakarta.validation.constraints.NotNull;

public class ApplicationRequestDto {
    @NotNull(message = "Candidate id is required")
    private Long candidateId;

    @NotNull(message = "Job description id is required")
    private Long jobDescriptionId;

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobDescriptionId() {
        return jobDescriptionId;
    }

    public void setJobDescriptionId(Long jobDescriptionId) {
        this.jobDescriptionId = jobDescriptionId;
    }
}