package com.recruitment.engine.service;

import com.recruitment.engine.dto.request.CandidateRequestDto;
import com.recruitment.engine.dto.request.CandidateUpdateFieldsRequestDto;
import com.recruitment.engine.dto.response.CandidateResponseDto;
import com.recruitment.engine.entity.Candidate;
import com.recruitment.engine.entity.User;
import com.recruitment.engine.exception.ResourceNotFoundException;
import com.recruitment.engine.repository.CandidateRepository;
import com.recruitment.engine.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public CandidateService(CandidateRepository candidateRepository, UserRepository userRepository) {
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
    }

    public CandidateResponseDto createCandidate(CandidateRequestDto request) {
        User currentUser = getCurrentUser();
        Candidate candidate = new Candidate(request.getName(), request.getEmail(), request.getPhone(), currentUser);
        return toDto(candidateRepository.save(candidate));
    }

    public CandidateResponseDto getCandidate(Long id) {
        return toDto(findOrThrow(id));
    }

    public List<CandidateResponseDto> getAllCandidates() {
        return candidateRepository.findAll().stream().map(this::toDto).toList();
    }

    public CandidateResponseDto updateCandidate(Long id, CandidateRequestDto request) {
        Candidate c = findOrThrow(id);
        c.setName(request.getName());
        c.setEmail(request.getEmail());
        c.setPhone(request.getPhone());
        return toDto(candidateRepository.save(c));
    }

    public CandidateResponseDto updateExtractedFields(Long id, CandidateUpdateFieldsRequestDto request) {
        Candidate c = findOrThrow(id);
        if (request.getSkills() != null)
            c.setSkills(request.getSkills());
        if (request.getExperienceYears() != null)
            c.setExperienceYears(request.getExperienceYears());
        if (request.getEducation() != null)
            c.setEducation(request.getEducation());
        if (request.getProjects() != null)
            c.setProjects(request.getProjects());
        if (request.getCertifications() != null)
            c.setCertifications(request.getCertifications());
        return toDto(candidateRepository.save(c));
    }

    public void deleteCandidate(Long id) {
        candidateRepository.delete(findOrThrow(id));
    }

    public List<CandidateResponseDto> search(String skill, String education, BigDecimal minExperience, String sortBy) {
        Sort sort = switch (sortBy == null ? "name" : sortBy) {
            case "experience" -> Sort.by(Sort.Direction.DESC, "experienceYears");
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.ASC, "name");
        };
        return candidateRepository.search(skill, education, minExperience, sort)
                .stream().map(this::toDto).toList();
    }

    public Candidate findOrThrow(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + email));
    }

    private CandidateResponseDto toDto(Candidate c) {
        return new CandidateResponseDto(c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getSkills(), c.getExperienceYears(), c.getEducation(), c.getProjects(),
                c.getCertifications(), c.getStatus().name(), c.getCreatedAt());
    }
}