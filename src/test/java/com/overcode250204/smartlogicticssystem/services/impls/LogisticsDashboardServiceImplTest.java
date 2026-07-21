package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.response.LogisticsDashboardResponse;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;
import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.repositories.OrderExceptionRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogisticsDashboardServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private OrderExceptionRepository orderExceptionRepository;

    private LogisticsDashboardServiceImpl service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-07-21T00:00:00Z"),
                ZoneId.of("Asia/Ho_Chi_Minh")
        );
        service = new LogisticsDashboardServiceImpl(
                orderRepository,
                vehicleRepository,
                orderExceptionRepository,
                clock
        );
    }

    @Test
    void getStatsReturnsSafeGrowthWhenPreviousPeriodHasNoData() {
        when(orderRepository.countOrders(any(), any())).thenReturn(12L, 0L);
        when(orderRepository.countDeliveredOrders(any(), any())).thenReturn(6L, 0L);
        when(orderRepository.countFinishedOrders(any(), any())).thenReturn(8L, 0L);
        when(orderExceptionRepository.countCriticalAlerts(any(), any())).thenReturn(2L, 0L);
        when(vehicleRepository.countByStatusAndVehicleTypeIn(eq(VehicleStatus.ON_TRIP), anyCollection()))
                .thenReturn(3L, 1L);

        LogisticsDashboardResponse result = service.getStats(LogisticsDashboardTimeFilter.MONTH);

        assertEquals(12L, result.getTotalOrders());
        assertEquals(100.0, result.getTotalOrdersGrowth());
        assertEquals(75.0, result.getSuccessRate());
        assertEquals(75.0, result.getSuccessRateGrowth());
        assertEquals(3L, result.getActiveFleetLinehaul());
        assertEquals(1L, result.getActiveFleetLocal());
        assertEquals(2L, result.getCriticalAlerts());

        verify(orderRepository).countOrders(
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 7, 22, 0, 0)
        );
        verify(orderRepository).countOrders(
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2026, 6, 22, 0, 0)
        );
    }

    @Test
    void getStatsReturnsZeroRatesWhenBothPeriodsAreEmpty() {
        when(orderRepository.countOrders(any(), any())).thenReturn(0L);
        when(orderRepository.countDeliveredOrders(any(), any())).thenReturn(0L);
        when(orderRepository.countFinishedOrders(any(), any())).thenReturn(0L);
        when(orderExceptionRepository.countCriticalAlerts(any(), any())).thenReturn(0L);
        when(vehicleRepository.countByStatusAndVehicleTypeIn(eq(VehicleStatus.ON_TRIP), anyCollection()))
                .thenReturn(0L);

        LogisticsDashboardResponse result = service.getStats(LogisticsDashboardTimeFilter.TODAY);

        assertEquals(0.0, result.getTotalOrdersGrowth());
        assertEquals(0.0, result.getSuccessRate());
        assertEquals(0.0, result.getSuccessRateGrowth());
    }

    @Test
    void resolvePeriodBuildsMatchingElapsedWeekRanges() {
        var period = LogisticsDashboardServiceImpl.resolvePeriod(
                LogisticsDashboardTimeFilter.WEEK,
                LocalDate.of(2026, 7, 21)
        );

        assertEquals(LocalDateTime.of(2026, 7, 20, 0, 0), period.current().from());
        assertEquals(LocalDateTime.of(2026, 7, 22, 0, 0), period.current().toExclusive());
        assertEquals(LocalDateTime.of(2026, 7, 13, 0, 0), period.previous().from());
        assertEquals(LocalDateTime.of(2026, 7, 15, 0, 0), period.previous().toExclusive());
    }
}
