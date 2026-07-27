package com.recruitment.engine.controller;

import com.recruitment.engine.dto.response.ResumeUploadResponseDto;
import com.recruitment.engine.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidates/{candidateId}/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<ResumeUploadResponseDto> upload(@PathVariable Long candidateId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(resumeService.uploadAndParse(candidateId, file));
    }
}