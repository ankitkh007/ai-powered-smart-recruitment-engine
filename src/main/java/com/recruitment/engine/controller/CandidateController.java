package com.recruitment.engine.controller;

import com.recruitment.engine.dto.request.CandidateRequestDto;
import com.recruitment.engine.dto.request.CandidateUpdateFieldsRequestDto;
import com.recruitment.engine.dto.response.CandidateResponseDto;
import com.recruitment.engine.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<CandidateResponseDto> create(@Valid @RequestBody CandidateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.createCandidate(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getCandidate(id));
    }

    @GetMapping
    public ResponseEntity<List<CandidateResponseDto>> getAll(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) BigDecimal minExperience,
            @RequestParam(required = false) String sortBy) {
        if (skill != null || education != null || minExperience != null || sortBy != null) {
            return ResponseEntity.ok(candidateService.search(skill, education, minExperience, sortBy));
        }
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateResponseDto> update(@PathVariable Long id,
            @Valid @RequestBody CandidateRequestDto request) {
        return ResponseEntity.ok(candidateService.updateCandidate(id, request));
    }

    @PatchMapping("/{id}/fields")
    public ResponseEntity<CandidateResponseDto> updateFields(@PathVariable Long id,
            @RequestBody CandidateUpdateFieldsRequestDto request) {
        return ResponseEntity.ok(candidateService.updateExtractedFields(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }
}