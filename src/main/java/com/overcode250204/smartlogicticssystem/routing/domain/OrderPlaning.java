package com.overcode250204.smartlogicticssystem.routing.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Data
@Builder
public class OrderPlaning implements Standstill{
    private Long id;
    private Point destination;
    private double weightKg;


    @Override public Point getLocation() { return destination; }
    @Override public Long getId() { return id; }

}