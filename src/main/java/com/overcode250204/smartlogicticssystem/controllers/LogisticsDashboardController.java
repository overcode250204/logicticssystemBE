package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.LogisticsDashboardResponse;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;
import com.overcode250204.smartlogicticssystem.services.ILogisticsDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logistics-dashboard")
@RequiredArgsConstructor
public class LogisticsDashboardController extends BaseController {

    private final ILogisticsDashboardService logisticsDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<LogisticsDashboardResponse>> getStats(
            @RequestParam(name = "timeFilter", defaultValue = "MONTH")
            LogisticsDashboardTimeFilter timeFilter) {
        return success(
                logisticsDashboardService.getStats(timeFilter),
                "Logistics dashboard retrieved successfully"
        );
    }
}
