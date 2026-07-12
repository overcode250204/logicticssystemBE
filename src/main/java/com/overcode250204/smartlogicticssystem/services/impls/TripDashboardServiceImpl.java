package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.response.TripDashboardDataResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.TripHistoryDTO;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import com.overcode250204.smartlogicticssystem.entities.LocalTrip;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.enums.LocalTripStatus;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.LocalTripRepository;
import com.overcode250204.smartlogicticssystem.services.ITripDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.overcode250204.smartlogicticssystem.repositories.LocalTripDetailRepository;

@Service
@RequiredArgsConstructor
public class TripDashboardServiceImpl implements ITripDashboardService {

    private final LinehaulTripRepository linehaulTripRepository;
    private final LocalTripRepository localTripRepository;
    private final LocalTripDetailRepository localTripDetailRepository;

    @Override
    public TripDashboardDataResponse getDashboardData(String type, int month, int year) {
        boolean isLinehaul = "LINEHAUL".equalsIgnoreCase(type);

        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        Map<String, Long> statusStats = new HashMap<>();
        long totalSelected = 0;
        long totalPrev = 0;
        List<TripDashboardDataResponse.ChartPoint> lineChartPoints = new ArrayList<>();
        List<TripDashboardDataResponse.BarChartPoint> barChartPoints = new ArrayList<>();
        List<TripDashboardDataResponse.TripWeightVolumePoint> weightVolumePoints = new ArrayList<>();

        if (isLinehaul) {
            // Linehaul
            List<LinehaulTrip> currentTrips = linehaulTripRepository.findByYearAndMonth(year, month);
            List<LinehaulTrip> prevTrips = linehaulTripRepository.findByYearAndMonth(prevYear, prevMonth);
            List<LinehaulTrip> yearlyTrips = linehaulTripRepository.findByYear(year);

            // Init status stats for linehaul
            for (LinehaulTripStatus status : LinehaulTripStatus.values()) {
                statusStats.put(status.name(), 0L);
            }
            for (LinehaulTrip trip : currentTrips) {
                statusStats.put(trip.getStatus().name(), statusStats.getOrDefault(trip.getStatus().name(), 0L) + 1);
            }

            totalSelected = currentTrips.size();
            totalPrev = prevTrips.size();

            // Line chart (Daily complete/cancel rate)
            int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
            Map<Integer, List<LinehaulTrip>> groupedByDay = currentTrips.stream()
                    .filter(t -> t.getDepartureTime() != null)
                    .collect(Collectors.groupingBy(t -> t.getDepartureTime().getDayOfMonth()));

            for (int day = 1; day <= daysInMonth; day++) {
                List<LinehaulTrip> dayTrips = groupedByDay.getOrDefault(day, Collections.emptyList());
                long complete = dayTrips.stream().filter(t -> LinehaulTripStatus.ARRIVED.equals(t.getStatus())).count();
                long cancel = dayTrips.stream().filter(t -> LinehaulTripStatus.CANCELLED.equals(t.getStatus())).count();
                double total = dayTrips.size();

                lineChartPoints.add(TripDashboardDataResponse.ChartPoint.builder()
                        .label(String.format("%02d", day))
                        .completeRate(total > 0 ? (complete / total * 100.0) : 0.0)
                        .cancelRate(total > 0 ? (cancel / total * 100.0) : 0.0)
                        .build());
            }

            // Weight & Volume per trip
            for (LinehaulTrip trip : currentTrips) {
                double weight = trip.getPallets().stream()
                        .map(p -> p.getTotalWeightKg() != null ? p.getTotalWeightKg().doubleValue() : 0.0)
                        .reduce(0.0, Double::sum);
                double volume = trip.getPallets().stream()
                        .map(p -> p.getTotalVolumeM3() != null ? p.getTotalVolumeM3().doubleValue() : 0.0)
                        .reduce(0.0, Double::sum);
                weightVolumePoints.add(TripDashboardDataResponse.TripWeightVolumePoint.builder()
                        .label(trip.getLinehaulTripCode() != null ? trip.getLinehaulTripCode() : "LH-" + trip.getLinehaulId())
                        .totalWeight(weight)
                        .totalVolume(volume)
                        .build());
            }

            // Bar chart (Monthly count for year)
            Map<Integer, Long> groupedByMonth = yearlyTrips.stream()
                    .filter(t -> t.getDepartureTime() != null)
                    .collect(Collectors.groupingBy(t -> t.getDepartureTime().getMonthValue(), Collectors.counting()));

            for (int m = 1; m <= 12; m++) {
                barChartPoints.add(TripDashboardDataResponse.BarChartPoint.builder()
                        .label("Tháng " + m)
                        .count(groupedByMonth.getOrDefault(m, 0L))
                        .build());
            }

        } else {
            // Local
            List<LocalTrip> currentTrips = localTripRepository.findByYearAndMonth(year, month);
            List<LocalTrip> prevTrips = localTripRepository.findByYearAndMonth(prevYear, prevMonth);
            List<LocalTrip> yearlyTrips = localTripRepository.findByYear(year);

            // Init status stats for local
            for (LocalTripStatus status : LocalTripStatus.values()) {
                statusStats.put(status.name(), 0L);
            }
            for (LocalTrip trip : currentTrips) {
                statusStats.put(trip.getStatus().name(), statusStats.getOrDefault(trip.getStatus().name(), 0L) + 1);
            }

            totalSelected = currentTrips.size();
            totalPrev = prevTrips.size();

            // Line chart (Daily complete/cancel rate)
            int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
            Map<Integer, List<LocalTrip>> groupedByDay = currentTrips.stream()
                    .filter(t -> t.getCreatedAt() != null)
                    .collect(Collectors.groupingBy(t -> t.getCreatedAt().getDayOfMonth()));

            for (int day = 1; day <= daysInMonth; day++) {
                List<LocalTrip> dayTrips = groupedByDay.getOrDefault(day, Collections.emptyList());
                long complete = dayTrips.stream().filter(t -> LocalTripStatus.COMPLETED.equals(t.getStatus())).count();
                long cancel = dayTrips.stream().filter(t -> LocalTripStatus.CANCELLED.equals(t.getStatus())).count();
                double total = dayTrips.size();

                lineChartPoints.add(TripDashboardDataResponse.ChartPoint.builder()
                        .label(String.format("%02d", day))
                        .completeRate(total > 0 ? (complete / total * 100.0) : 0.0)
                        .cancelRate(total > 0 ? (cancel / total * 100.0) : 0.0)
                        .build());
            }

            // Weight & Volume per trip
            for (LocalTrip trip : currentTrips) {
                List<com.overcode250204.smartlogicticssystem.entities.LocalTripDetail> details = 
                        localTripDetailRepository.findByLocalTrip_LocalTripId(trip.getLocalTripId());
                double weight = 0.0;
                double volume = 0.0;
                if (details != null) {
                    for (com.overcode250204.smartlogicticssystem.entities.LocalTripDetail d : details) {
                        if (d.getOrder() != null) {
                            if (d.getOrder().getTotalWeightKg() != null) {
                                weight += d.getOrder().getTotalWeightKg().doubleValue();
                            }
                            if (d.getOrder().getTotalVolumeM3() != null) {
                                volume += d.getOrder().getTotalVolumeM3().doubleValue();
                            }
                        }
                    }
                }
                weightVolumePoints.add(TripDashboardDataResponse.TripWeightVolumePoint.builder()
                        .label(trip.getLocalTripCode() != null ? trip.getLocalTripCode() : "LC-" + trip.getLocalTripId())
                        .totalWeight(weight)
                        .totalVolume(volume)
                        .build());
            }

            // Bar chart (Monthly count for year)
            Map<Integer, Long> groupedByMonth = yearlyTrips.stream()
                    .filter(t -> t.getCreatedAt() != null)
                    .collect(Collectors.groupingBy(t -> t.getCreatedAt().getMonthValue(), Collectors.counting()));

            for (int m = 1; m <= 12; m++) {
                barChartPoints.add(TripDashboardDataResponse.BarChartPoint.builder()
                        .label("Tháng " + m)
                        .count(groupedByMonth.getOrDefault(m, 0L))
                        .build());
            }
        }

        double growthRate = 0.0;
        if (totalPrev > 0) {
            growthRate = ((double) (totalSelected - totalPrev) / totalPrev) * 100.0;
        } else if (totalSelected > 0) {
            growthRate = 100.0;
        }

        return TripDashboardDataResponse.builder()
                .statusStats(statusStats)
                .totalTripsSelectedMonth(totalSelected)
                .totalTripsPreviousMonth(totalPrev)
                .growthRate(growthRate)
                .lineChartPoints(lineChartPoints)
                .barChartPoints(barChartPoints)
                .weightVolumePoints(weightVolumePoints)
                .build();
    }

    @Override
    public Page<TripHistoryDTO> getTripHistory(String type, int month, int year, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        boolean isLinehaul = "LINEHAUL".equalsIgnoreCase(type);

        String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : "%" + keyword.trim().toLowerCase() + "%";

        if (isLinehaul) {
            Page<LinehaulTrip> tripsPage = linehaulTripRepository.searchLinehaulTrips(year, month, searchKeyword, pageable);
            List<TripHistoryDTO> dtoList = tripsPage.getContent().stream()
                    .map(t -> {
                        String driverName = t.getTripDrivers().stream()
                                .map(d -> d.getDriver().getName())
                                .collect(Collectors.joining(", "));
                        String details = t.getRouteConfig() != null ? t.getRouteConfig().getRouteName() : "N/A";
                        return TripHistoryDTO.builder()
                                .id(t.getLinehaulId())
                                .tripCode(t.getLinehaulTripCode())
                                .tripType("LINEHAUL")
                                .driverName(driverName.isEmpty() ? "N/A" : driverName)
                                .vehiclePlate(t.getVehicle() != null ? t.getVehicle().getLicensePlate() : "N/A")
                                .status(t.getStatus().name())
                                .time(t.getDepartureTime())
                                .details(details)
                                .build();
                    }).toList();
            return new PageImpl<>(dtoList, pageable, tripsPage.getTotalElements());
        } else {
            Page<LocalTrip> tripsPage = localTripRepository.searchLocalTrips(year, month, searchKeyword, pageable);
            List<TripHistoryDTO> dtoList = tripsPage.getContent().stream()
                    .map(t -> {
                        String driverName = t.getDriver() != null ? t.getDriver().getName() : "N/A";
                        String details = t.getHub() != null ? t.getHub().getName() : "N/A";
                        return TripHistoryDTO.builder()
                                .id(t.getLocalTripId())
                                .tripCode(t.getLocalTripCode())
                                .tripType("LOCAL")
                                .driverName(driverName)
                                .vehiclePlate(t.getVehicle() != null ? t.getVehicle().getLicensePlate() : "N/A")
                                .status(t.getStatus().name())
                                .time(t.getCreatedAt())
                                .details(details)
                                .build();
                    }).toList();
            return new PageImpl<>(dtoList, pageable, tripsPage.getTotalElements());
        }
    }
}
