package com.overcode250204.smartlogicticssystem.routing.domain;

import ai.timefold.solver.core.api.domain.solution.*;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@PlanningSolution
public class RoutePlanSolution {

    @ProblemFactProperty
    private DepotSetting depot;


    @PlanningEntityCollectionProperty
    private List<Driver> driverList;

    @ValueRangeProvider(id = "orderRange")
    @ProblemFactCollectionProperty
    private List<OrderPlaning> orderPlaningList;

    @ProblemFactProperty
    private DistanceMatrix osrmDistanceMatrix;

    @PlanningScore
    private HardSoftScore score;

    public RoutePlanSolution(DepotSetting depot, List<Driver> drivers, List<OrderPlaning> orderPlanings,DistanceMatrix distanceMatrix) {
        this.depot = depot;
        this.driverList = drivers;
        this.orderPlaningList = orderPlanings;
        this.osrmDistanceMatrix = distanceMatrix;
    }

    public RoutePlanSolution() {
    }

}