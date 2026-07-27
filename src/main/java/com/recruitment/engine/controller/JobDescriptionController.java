package com.recruitment.engine.controller;

import com.recruitment.engine.dto.request.JobDescriptionRequestDto;
import com.recruitment.engine.dto.request.JobStatusUpdateRequestDto;
import com.recruitment.engine.dto.response.JobDescriptionResponseDto;
import com.recruitment.engine.service.JobDescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    public JobDescriptionController(JobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    @PostMapping
    public ResponseEntity<JobDescriptionResponseDto> createJob(
            @Valid @RequestBody JobDescriptionRequestDto request) {
        JobDescriptionResponseDto created = jobDescriptionService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDescriptionResponseDto> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobDescriptionService.getJob(id));
    }

    @GetMapping
    public ResponseEntity<List<JobDescriptionResponseDto>> getAllJobs() {
        return ResponseEntity.ok(jobDescriptionService.getAllJobs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDescriptionResponseDto> updateJob(
            @PathVariable Long id, @Valid @RequestBody JobDescriptionRequestDto request) {
        return ResponseEntity.ok(jobDescriptionService.updateJob(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobDescriptionResponseDto> updateStatus(
            @PathVariable Long id, @Valid @RequestBody JobStatusUpdateRequestDto request) {
        return ResponseEntity.ok(jobDescriptionService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobDescriptionService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}