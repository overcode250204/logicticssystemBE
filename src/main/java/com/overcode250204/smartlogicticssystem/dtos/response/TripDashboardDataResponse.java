package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TripDashboardDataResponse {
    private Map<String, Long> statusStats;
    private Long totalTripsSelectedMonth;
    private Long totalTripsPreviousMonth;
    private Double growthRate;
    private List<ChartPoint> lineChartPoints;
    private List<BarChartPoint> barChartPoints;
    private List<TripWeightVolumePoint> weightVolumePoints;

    @Data
    @Builder
    public static class TripWeightVolumePoint {
        private String label;
        private Double totalWeight;
        private Double totalVolume;
    }

    @Data
    @Builder
    public static class ChartPoint {
        private String label;
        private Double completeRate;
        private Double cancelRate;
    }

    @Data
    @Builder
    public static class BarChartPoint {
        private String label;
        private Long count;
    }
}
