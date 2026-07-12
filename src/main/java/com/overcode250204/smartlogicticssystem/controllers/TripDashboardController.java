package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.TripDashboardDataResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.TripHistoryDTO;
import com.overcode250204.smartlogicticssystem.services.ITripDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip-dashboard")
@Slf4j
@RequiredArgsConstructor
public class TripDashboardController extends BaseController {

    private final ITripDashboardService tripDashboardService;

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<TripDashboardDataResponse>> getStats(
            @RequestParam(name = "type", defaultValue = "LINEHAUL") String type,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        return success(tripDashboardService.getDashboardData(type, month, year), "Stats retrieved successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<BaseResponse<Page<TripHistoryDTO>>> getHistory(
            @RequestParam(name = "type", defaultValue = "LINEHAUL") String type,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return success(tripDashboardService.getTripHistory(type, month, year, keyword, page, size), "Trip history retrieved successfully");
    }
}
