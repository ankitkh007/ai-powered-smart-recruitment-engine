package com.recruitment.engine.service;

import com.recruitment.engine.dto.response.CandidateResponseDto;
import com.recruitment.engine.dto.response.DashboardSummaryResponseDto;
import com.recruitment.engine.entity.Candidate;
import com.recruitment.engine.entity.enums.CandidateStatus;
import com.recruitment.engine.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final CandidateRepository candidateRepository;

    public DashboardService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public DashboardSummaryResponseDto getSummary() {
        long total = candidateRepository.count();
        long pending = candidateRepository.countByStatusIn(
                List.of(CandidateStatus.UPLOADED, CandidateStatus.PARSED, CandidateStatus.SCORING_PENDING));

        List<CandidateResponseDto> recent = candidateRepository.findTop10ByOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();

        return new DashboardSummaryResponseDto(total, pending, recent);
    }

    private CandidateResponseDto toDto(Candidate c) {
        return new CandidateResponseDto(c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getSkills(), c.getExperienceYears(), c.getEducation(), c.getProjects(),
                c.getCertifications(), c.getStatus().name(), c.getCreatedAt());
    }
}