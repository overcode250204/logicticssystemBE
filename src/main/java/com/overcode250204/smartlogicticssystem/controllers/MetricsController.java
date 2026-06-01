package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.FinancialSummaryResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IFinancialMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController extends BaseController {
    private final IFinancialMetricsService financialMetricsService;

    @GetMapping("/summary")
    public ResponseEntity<BaseResponse<FinancialSummaryResponseDTO>> getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        return success(financialMetricsService.getSummary(month, year), "Financial summary retrieved successfully");
    }
}
