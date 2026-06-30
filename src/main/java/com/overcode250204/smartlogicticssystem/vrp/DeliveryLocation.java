package com.overcode250204.smartlogicticssystem.vrp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLocation {
    private Long id;
    private double lat;
    private double lon;
    
    // Using a map for pre-calculated distances to other locations
    private Map<DeliveryLocation, Double> distanceMap;
    
    public DeliveryLocation(Long id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public double getDistanceTo(DeliveryLocation location) {
        if (this == location) {
            return 0d;
        }
        return distanceMap != null ? distanceMap.getOrDefault(location, Double.MAX_VALUE) : Double.MAX_VALUE;
    }
}
