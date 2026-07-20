package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.DispatchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class RouteConfigCreateRequest {
    @NotBlank(message = "Route name is required")
    private String routeName;

    @NotNull(message = "From warehouse ID is required")
    private Long fromWarehouseId;

    @NotNull(message = "To warehouse ID is required")
    private Long toWarehouseId;

    @NotNull(message = "Dispatch type is required")
    private DispatchType dispatchType;

    private LocalTime fixedDispatchTime;

    private Integer minCapacityPercentage;

    @NotNull(message = "Cutoff time is required")
    private LocalTime cutoffTime;

    private Integer maxWaitingDays;

    private Long defaultVehicleId;

    private List<String> provinceNames;

    private Boolean isActive = true;
}
