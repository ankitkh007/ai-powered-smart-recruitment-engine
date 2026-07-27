package com.recruitment.engine.service;

import com.recruitment.engine.dto.response.ResumeUploadResponseDto;
import com.recruitment.engine.entity.Candidate;
import com.recruitment.engine.entity.Resume;
import com.recruitment.engine.entity.enums.CandidateStatus;
import com.recruitment.engine.exception.InvalidFileException;
import com.recruitment.engine.exception.AIProcessingException;
import com.recruitment.engine.repository.CandidateRepository;
import com.recruitment.engine.repository.ResumeRepository;
import com.recruitment.engine.service.ai.ParsedResumeData;
import com.recruitment.engine.service.ai.ResumeParsingService;
import com.recruitment.engine.util.PdfTextExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final CandidateRepository candidateRepository;
    private final PdfTextExtractor pdfTextExtractor;
    private final ResumeParsingService resumeParsingService;

    @Value("${resume.upload-dir}")
    private String uploadDir;

    @Value("${resume.max-size-bytes}")
    private long maxSizeBytes;

    public ResumeService(ResumeRepository resumeRepository, CandidateRepository candidateRepository,
            PdfTextExtractor pdfTextExtractor, ResumeParsingService resumeParsingService) {
        this.resumeRepository = resumeRepository;
        this.candidateRepository = candidateRepository;
        this.pdfTextExtractor = pdfTextExtractor;
        this.resumeParsingService = resumeParsingService;
    }

    public ResumeUploadResponseDto uploadAndParse(Long candidateId, MultipartFile file) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new com.recruitment.engine.exception.ResourceNotFoundException(
                        "Candidate not found with id: " + candidateId));

        validateFile(file);

        String savedPath = saveFile(candidateId, file);
        Resume resume = new Resume(candidate, savedPath, file.getSize());
        resumeRepository.save(resume);

        try {
            String rawText = pdfTextExtractor.extractText(new File(savedPath));
            if (rawText == null || rawText.isBlank()) {
                candidate.setStatus(CandidateStatus.FAILED);
                candidateRepository.save(candidate);
                throw new InvalidFileException("Resume PDF contains no extractable text");
            }

            ParsedResumeData parsed = resumeParsingService.parseResume(rawText);

            candidate.setSkills(parsed.skills());
            candidate.setExperienceYears(
                    parsed.experienceYears() != null ? BigDecimal.valueOf(parsed.experienceYears()) : null);
            candidate.setEducation(parsed.education());
            candidate.setProjects(parsed.projects());
            candidate.setCertifications(parsed.certifications());
            candidate.setStatus(CandidateStatus.PARSED);
            candidateRepository.save(candidate);

            return new ResumeUploadResponseDto(candidateId, "PARSED", "Resume uploaded and parsed successfully");

        } catch (AIProcessingException e) {
            candidate.setStatus(CandidateStatus.FAILED);
            candidateRepository.save(candidate);
            throw e;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty");
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileException("Only PDF files are allowed");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new InvalidFileException("File size exceeds maximum allowed (10MB)");
        }
    }

    private String saveFile(Long candidateId, MultipartFile file) {
        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String fileName = "candidate_" + candidateId + "_" + System.currentTimeMillis() + ".pdf";
            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            return filePath.toString();
        } catch (IOException e) {
            throw new InvalidFileException("Failed to save uploaded file: " + e.getMessage());
        }
    }
}