package com.overcode250204.smartlogicticssystem.vrp;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@PlanningSolution
@Getter
@Setter
@NoArgsConstructor
public class DeliverySolution {

    @ProblemFactCollectionProperty
    private List<DeliveryLocation> locationList;

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<DeliveryOrder> orderList;

    @PlanningEntityCollectionProperty
    private List<DeliveryVehicle> vehicleList;

    @PlanningScore
    private HardSoftLongScore score;
    
    public DeliverySolution(List<DeliveryLocation> locationList, List<DeliveryOrder> orderList, List<DeliveryVehicle> vehicleList) {
        this.locationList = locationList;
        this.orderList = orderList;
        this.vehicleList = vehicleList;
    }
}
