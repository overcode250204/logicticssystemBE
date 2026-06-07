package com.overcode250204.smartlogicticssystem.routing.domain;

import lombok.Builder;
import org.locationtech.jts.geom.Point;


@Builder
public class DepotSetting implements Standstill {
    private Long id;
    private Point location;

    @Override public Point getLocation() { return location; }
    @Override public Long getId() { return id; }
    // getters, setters
}