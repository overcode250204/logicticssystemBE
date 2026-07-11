package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.DispatchType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class RouteConfigResponseDTO {
    private Long routeId;
    private String routeName;
    private WarehouseResponseDTO fromWarehouse;
    private WarehouseResponseDTO toWarehouse;
    private DispatchType dispatchType;
    private VehicleResponseDTO defaultVehicle;
    private LocalTime fixedDispatchTime;
    private Integer minCapacityPercentage;
    private LocalTime cutoffTime;
    private Integer maxWaitingDays;
    private List<String> provinceNames;
    private Boolean isActive;
}
