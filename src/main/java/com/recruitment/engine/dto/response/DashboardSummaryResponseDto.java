package com.recruitment.engine.dto.response;

import java.util.List;

public class DashboardSummaryResponseDto {
    private long totalCandidates;
    private long pendingAiAnalysis;
    private List<CandidateResponseDto> recentUploads;

    public DashboardSummaryResponseDto(long totalCandidates, long pendingAiAnalysis,
            List<CandidateResponseDto> recentUploads) {
        this.totalCandidates = totalCandidates;
        this.pendingAiAnalysis = pendingAiAnalysis;
        this.recentUploads = recentUploads;
    }

    public long getTotalCandidates() {
        return totalCandidates;
    }

    public long getPendingAiAnalysis() {
        return pendingAiAnalysis;
    }

    public List<CandidateResponseDto> getRecentUploads() {
        return recentUploads;
    }
}