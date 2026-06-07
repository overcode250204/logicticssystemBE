package com.overcode250204.smartlogicticssystem.routing.domain;

import org.locationtech.jts.geom.Point;

public interface Standstill {
    Point getLocation(); // Trả về tọa độ PostGIS Point
    Long getId();
}