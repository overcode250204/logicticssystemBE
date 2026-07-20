package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.LogisticsDashboardResponse;
import com.overcode250204.smartlogicticssystem.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@RestController
@RequestMapping("/api/admin/logistics-dashboard")
@Slf4j
@RequiredArgsConstructor
public class LogisticsDashboardController extends BaseController {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ZoneRepository zoneRepository;
    private final LinehaulTripRepository linehaulTripRepository;
    private final OrderExceptionRepository orderExceptionRepository;

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<LogisticsDashboardResponse>> getStats(@RequestParam LocalDate from, @RequestParam LocalDate to) {

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.plusDays(1).atStartOfDay();
        Long totalOrders = orderRepository.countOrders(fromDate, toDate);

        Long delivered =
                orderRepository.countDeliveredOrders(fromDate, toDate);

        Long finished =
                orderRepository.countFinishedOrders(fromDate, toDate);

        Double successRate = finished == 0
                ? 0
                : delivered * 100.0 / finished;

        Long activeFleet =
                vehicleRepository.countActiveFleet();

        Long criticalAlerts =
                orderExceptionRepository.countCriticalAlerts(fromDate, toDate);

        //Calculate change
        long days = ChronoUnit.DAYS.between(from, to) + 1;

        LocalDate previousStart = from.minusDays(days);

        LocalDate previousEnd = to.minusDays(1);

        LocalDateTime previousStartDate = previousStart.atStartOfDay();
        LocalDateTime previousEndDate = previousEnd.plusDays(1).atStartOfDay();

        Long prevTotalOrders = orderRepository.countOrders(previousStartDate, previousEndDate);

        Long prevDelivered = orderRepository.countDeliveredOrders(previousStartDate, previousEndDate);

        Long prevFinished = orderRepository.countFinishedOrders(previousStartDate, previousEndDate);

        Double prevSuccessRate = finished == 0
                ? 0
                : prevDelivered * 100.0 / prevFinished;

        Double totalOrderRateChange = ((totalOrders - prevTotalOrders) / prevTotalOrders) * 100.0;


        return success(new LogisticsDashboardResponse(
                totalOrders,
                totalOrderRateChange,
                successRate,
                activeFleet,
                criticalAlerts,
                successRate - prevSuccessRate
        ),"Get dashboar success");
    }
}
