package com.overcode250204.smartlogicticssystem.vrp;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryVehicle {
    private Long vehicleId;
    private Long driverId; // Assigned driver
    private DeliveryLocation startLocation; // Hub location
    private BigDecimal maxWeightKg;
    private BigDecimal maxVolumeM3;

    @PlanningListVariable
    private List<DeliveryOrder> orders = new ArrayList<>();
}
