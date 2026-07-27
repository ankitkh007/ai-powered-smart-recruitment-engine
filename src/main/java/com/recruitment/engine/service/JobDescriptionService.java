package com.recruitment.engine.service;

import com.recruitment.engine.dto.request.JobDescriptionRequestDto;
import com.recruitment.engine.dto.request.JobStatusUpdateRequestDto;
import com.recruitment.engine.dto.response.JobDescriptionResponseDto;
import com.recruitment.engine.entity.JobDescription;
import com.recruitment.engine.entity.User;
import com.recruitment.engine.entity.enums.JobStatus;
import com.recruitment.engine.exception.ResourceNotFoundException;
import com.recruitment.engine.repository.JobDescriptionRepository;
import com.recruitment.engine.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JobDescriptionService {

    private static final Map<JobStatus, Set<JobStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(JobStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(JobStatus.DRAFT, EnumSet.of(JobStatus.OPEN, JobStatus.CLOSED));
        ALLOWED_TRANSITIONS.put(JobStatus.OPEN, EnumSet.of(JobStatus.CLOSED));
        ALLOWED_TRANSITIONS.put(JobStatus.CLOSED, EnumSet.noneOf(JobStatus.class));
    }

    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;

    public JobDescriptionService(JobDescriptionRepository jobDescriptionRepository,
            UserRepository userRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.userRepository = userRepository;
    }

    public JobDescriptionResponseDto createJob(JobDescriptionRequestDto request) {
        User currentUser = getCurrentUser();

        JobDescription job = new JobDescription(
                request.getTitle(),
                request.getDescription(),
                request.getRequiredSkills(),
                currentUser);

        return toResponseDto(jobDescriptionRepository.save(job));
    }

    public JobDescriptionResponseDto getJob(Long id) {
        return toResponseDto(findJobOrThrow(id));
    }

    public List<JobDescriptionResponseDto> getAllJobs() {
        return jobDescriptionRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public JobDescriptionResponseDto updateJob(Long id, JobDescriptionRequestDto request) {
        JobDescription job = findJobOrThrow(id);

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new IllegalStateException("Cannot edit a job description that is already CLOSED");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills());

        return toResponseDto(jobDescriptionRepository.save(job));
    }

    public JobDescriptionResponseDto updateStatus(Long id, JobStatusUpdateRequestDto request) {
        JobDescription job = findJobOrThrow(id);
        JobStatus currentStatus = job.getStatus();
        JobStatus newStatus = request.getStatus();

        if (!ALLOWED_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition job status from " + currentStatus + " to " + newStatus);
        }

        job.setStatus(newStatus);
        return toResponseDto(jobDescriptionRepository.save(job));
    }

    public void deleteJob(Long id) {
        JobDescription job = findJobOrThrow(id);
        jobDescriptionRepository.delete(job);
    }

    private JobDescription findJobOrThrow(Long id) {
        return jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job description not found with id: " + id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + email));
    }

    private JobDescriptionResponseDto toResponseDto(JobDescription job) {
        return new JobDescriptionResponseDto(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getRequiredSkills(),
                job.getStatus().name(),
                job.getCreatedBy().getEmail(),
                job.getCreatedAt(),
                job.getUpdatedAt());
    }
}