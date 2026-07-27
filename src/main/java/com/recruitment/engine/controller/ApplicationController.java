package com.recruitment.engine.controller;

import com.recruitment.engine.dto.request.ApplicationRequestDto;
import com.recruitment.engine.dto.response.ApplicationResponseDto;
import com.recruitment.engine.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponseDto> create(@Valid @RequestBody ApplicationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(request));
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplication(id));
    }

    @PostMapping("/applications/{id}/score")
    public ResponseEntity<ApplicationResponseDto> score(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.scoreApplication(id));
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<ApplicationResponseDto>> rankedForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getRankedApplicationsForJob(jobId));
    }
}