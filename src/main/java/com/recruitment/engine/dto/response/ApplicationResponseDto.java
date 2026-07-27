package com.recruitment.engine.dto.response;

public class ApplicationResponseDto {
    private Long id;
    private Long candidateId;
    private String candidateName;
    private Long jobDescriptionId;
    private String jobTitle;
    private String status;
    private AIScoreResponseDto aiScore;

    public ApplicationResponseDto(Long id, Long candidateId, String candidateName, Long jobDescriptionId,
            String jobTitle, String status, AIScoreResponseDto aiScore) {
        this.id = id;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.jobDescriptionId = jobDescriptionId;
        this.jobTitle = jobTitle;
        this.status = status;
        this.aiScore = aiScore;
    }

    public Long getId() {
        return id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public Long getJobDescriptionId() {
        return jobDescriptionId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getStatus() {
        return status;
    }

    public AIScoreResponseDto getAiScore() {
        return aiScore;
    }
}