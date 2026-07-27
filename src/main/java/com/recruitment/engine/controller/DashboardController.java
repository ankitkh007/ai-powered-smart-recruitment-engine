package com.recruitment.engine.controller;

import com.recruitment.engine.dto.response.DashboardSummaryResponseDto;
import com.recruitment.engine.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponseDto> summary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}