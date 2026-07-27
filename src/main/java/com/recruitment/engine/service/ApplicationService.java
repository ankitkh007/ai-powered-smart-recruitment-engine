package com.recruitment.engine.service;

import com.recruitment.engine.dto.request.ApplicationRequestDto;
import com.recruitment.engine.dto.response.AIScoreResponseDto;
import com.recruitment.engine.dto.response.ApplicationResponseDto;
import com.recruitment.engine.entity.*;
import com.recruitment.engine.entity.enums.ApplicationStatus;
import com.recruitment.engine.entity.enums.CandidateStatus;
import com.recruitment.engine.entity.enums.ConfidenceLevel;
import com.recruitment.engine.entity.enums.JobStatus;
import com.recruitment.engine.exception.AIProcessingException;
import com.recruitment.engine.exception.DuplicateResourceException;
import com.recruitment.engine.exception.ResourceNotFoundException;
import com.recruitment.engine.repository.*;
import com.recruitment.engine.service.ai.AIScoreResult;
import com.recruitment.engine.service.ai.AIScoringService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final AIScoreRepository aiScoreRepository;
    private final AIScoringService aiScoringService;

    public ApplicationService(ApplicationRepository applicationRepository,
            CandidateRepository candidateRepository,
            JobDescriptionRepository jobDescriptionRepository,
            AIScoreRepository aiScoreRepository,
            AIScoringService aiScoringService) {
        this.applicationRepository = applicationRepository;
        this.candidateRepository = candidateRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.aiScoreRepository = aiScoreRepository;
        this.aiScoringService = aiScoringService;
    }

    public ApplicationResponseDto createApplication(ApplicationRequestDto request) {
        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        JobDescription job = jobDescriptionRepository.findById(request.getJobDescriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Job description not found"));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new IllegalStateException("Cannot apply to a job that is not OPEN");
        }

        applicationRepository.findByCandidateIdAndJobDescriptionId(candidate.getId(), job.getId())
                .ifPresent(a -> {
                    throw new DuplicateResourceException("Application already exists for this candidate and job");
                });

        Application application = new Application(candidate, job);
        return toDto(applicationRepository.save(application));
    }

    public ApplicationResponseDto getApplication(Long id) {
        return toDto(findOrThrow(id));
    }

    public List<ApplicationResponseDto> getRankedApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobIdRankedByScore(jobId).stream().map(this::toDto).toList();
    }

    public ApplicationResponseDto scoreApplication(Long applicationId) {
        Application application = findOrThrow(applicationId);
        Candidate candidate = application.getCandidate();
        JobDescription job = application.getJobDescription();

        candidate.setStatus(CandidateStatus.SCORING_PENDING);
        candidateRepository.save(candidate);

        try {
            String candidateProfile = buildCandidateProfileText(candidate);
            String jobText = job.getTitle() + "\n" + job.getDescription() + "\nRequired Skills: "
                    + job.getRequiredSkills();

            AIScoreResult result = aiScoringService.scoreCandidate(candidateProfile, jobText);

            AIScore aiScore = new AIScore(
                    application,
                    result.matchPercentage(),
                    ConfidenceLevel.valueOf(result.confidence()),
                    result.matchingSkills(),
                    result.missingSkills(),
                    result.strengths(),
                    result.weaknesses(),
                    result.summary());
            aiScoreRepository.save(aiScore);

            application.setStatus(ApplicationStatus.SCORED);
            applicationRepository.save(application);

            candidate.setStatus(CandidateStatus.SCORED);
            candidateRepository.save(candidate);

            return toDto(application);

        } catch (AIProcessingException e) {
            application.setStatus(ApplicationStatus.FAILED);
            applicationRepository.save(application);
            candidate.setStatus(CandidateStatus.FAILED);
            candidateRepository.save(candidate);
            throw e;
        }
    }

    private String buildCandidateProfileText(Candidate c) {
        return "Skills: " + c.getSkills() + "\nExperience: " + c.getExperienceYears() + " years"
                + "\nEducation: " + c.getEducation() + "\nProjects: " + c.getProjects()
                + "\nCertifications: " + c.getCertifications();
    }

    private Application findOrThrow(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    private ApplicationResponseDto toDto(Application a) {
        AIScoreResponseDto scoreDto = aiScoreRepository.findByApplicationId(a.getId())
                .map(s -> new AIScoreResponseDto(s.getMatchPercentage(), s.getConfidence().name(),
                        s.getMatchingSkills(), s.getMissingSkills(), s.getStrengths(), s.getWeaknesses(),
                        s.getSummary()))
                .orElse(null);

        return new ApplicationResponseDto(
                a.getId(), a.getCandidate().getId(), a.getCandidate().getName(),
                a.getJobDescription().getId(), a.getJobDescription().getTitle(),
                a.getStatus().name(), scoreDto);
    }
}