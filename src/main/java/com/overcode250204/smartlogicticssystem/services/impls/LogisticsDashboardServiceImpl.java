package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.response.LogisticsDashboardResponse;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;
import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.enums.VehicleType;
import com.overcode250204.smartlogicticssystem.repositories.OrderExceptionRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.services.ILogisticsDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogisticsDashboardServiceImpl implements ILogisticsDashboardService {

    private static final List<VehicleType> LINEHAUL_VEHICLE_TYPES = List.of(
            VehicleType.SMALL_TRUCK,
            VehicleType.BIG_TRUCK
    );
    private static final List<VehicleType> LOCAL_VEHICLE_TYPES = List.of(VehicleType.BIKE);

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderExceptionRepository orderExceptionRepository;
    private final Clock clock;

    @Override
    public LogisticsDashboardResponse getStats(LogisticsDashboardTimeFilter timeFilter) {
        DashboardPeriod period = resolvePeriod(timeFilter, LocalDate.now(clock));
        DashboardMetrics current = loadMetrics(period.current());
        DashboardMetrics previous = loadMetrics(period.previous());

        long activeFleetLinehaul = vehicleRepository.countByStatusAndVehicleTypeIn(
                VehicleStatus.ON_TRIP,
                LINEHAUL_VEHICLE_TYPES
        );
        long activeFleetLocal = vehicleRepository.countByStatusAndVehicleTypeIn(
                VehicleStatus.ON_TRIP,
                LOCAL_VEHICLE_TYPES
        );

        return LogisticsDashboardResponse.builder()
                .totalOrders(current.totalOrders())
                .totalOrdersGrowth(percentageChange(current.totalOrders(), previous.totalOrders()))
                .successRate(current.successRate())
                .successRateGrowth(current.successRate() - previous.successRate())
                .activeFleetLinehaul(activeFleetLinehaul)
                .activeFleetLocal(activeFleetLocal)
                .criticalAlerts(current.criticalAlerts())
                .build();
    }

    private DashboardMetrics loadMetrics(DateRange range) {
        long totalOrders = orderRepository.countOrders(range.from(), range.toExclusive());
        long deliveredOrders = orderRepository.countDeliveredOrders(range.from(), range.toExclusive());
        long finishedOrders = orderRepository.countFinishedOrders(range.from(), range.toExclusive());
        long criticalAlerts = orderExceptionRepository.countCriticalAlerts(range.from(), range.toExclusive());

        return new DashboardMetrics(
                totalOrders,
                percentage(deliveredOrders, finishedOrders),
                criticalAlerts
        );
    }

    static DashboardPeriod resolvePeriod(LogisticsDashboardTimeFilter timeFilter, LocalDate today) {
        LocalDate currentStart;
        LocalDate previousStart;
        LocalDate previousEndExclusive;
        LocalDate currentEndExclusive = today.plusDays(1);

        switch (timeFilter) {
            case TODAY -> {
                currentStart = today;
                previousStart = today.minusDays(1);
                previousEndExclusive = today;
            }
            case WEEK -> {
                currentStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                int elapsedDays = today.getDayOfWeek().getValue();
                previousStart = currentStart.minusWeeks(1);
                previousEndExclusive = previousStart.plusDays(elapsedDays);
            }
            case MONTH -> {
                currentStart = today.withDayOfMonth(1);
                previousStart = currentStart.minusMonths(1);
                int elapsedDays = Math.min(today.getDayOfMonth(), previousStart.lengthOfMonth());
                previousEndExclusive = previousStart.plusDays(elapsedDays);
            }
            default -> throw new IllegalArgumentException("Unsupported dashboard time filter: " + timeFilter);
        }

        return new DashboardPeriod(
                DateRange.of(currentStart, currentEndExclusive),
                DateRange.of(previousStart, previousEndExclusive)
        );
    }

    private static double percentage(long value, long total) {
        return total == 0 ? 0.0 : value * 100.0 / total;
    }

    private static double percentageChange(long current, long previous) {
        if (previous == 0) {
            return current == 0 ? 0.0 : 100.0;
        }
        return (current - previous) * 100.0 / previous;
    }

    record DateRange(LocalDateTime from, LocalDateTime toExclusive) {
        static DateRange of(LocalDate from, LocalDate toExclusive) {
            return new DateRange(from.atStartOfDay(), toExclusive.atStartOfDay());
        }
    }

    record DashboardPeriod(DateRange current, DateRange previous) {
    }

    private record DashboardMetrics(long totalOrders, double successRate, long criticalAlerts) {
    }
}
