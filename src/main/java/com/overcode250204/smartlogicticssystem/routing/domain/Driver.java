package com.overcode250204.smartlogicticssystem.routing.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@PlanningEntity
@NoArgsConstructor
@AllArgsConstructor
public class Driver implements Standstill {
    private Long id;
    private double maxWeightCapacity;
    private DepotSetting depot;

    @PlanningListVariable(valueRangeProviderRefs = "orderRange")
    private List<OrderPlaning> orderPlaningList = new ArrayList<>();

    @Override public Point getLocation() { return depot.getLocation(); }
    @Override public Long getId() { return id; }


}