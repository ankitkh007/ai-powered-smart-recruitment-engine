package com.recruitment.engine.dto.response;

public class ResumeUploadResponseDto {
    private Long candidateId;
    private String status;
    private String message;

    public ResumeUploadResponseDto(Long candidateId, String status, String message) {
        this.candidateId = candidateId;
        this.status = status;
        this.message = message;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}