package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.response.ActiveVehicleInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveTrackingCache {
    private final Map<String, ActiveVehicleInfo> activeVehicles = new ConcurrentHashMap<>();

    public void put(String tripId, ActiveVehicleInfo info) {
        activeVehicles.put(tripId, info);
    }

    public ActiveVehicleInfo get(String tripId) {
        return activeVehicles.get(tripId);
    }

    public boolean contains(String tripId) {
        return activeVehicles.containsKey(tripId);
    }

    public void remove(String tripId) {
        activeVehicles.remove(tripId);
    }

    public List<ActiveVehicleInfo> getAll() {
        return new ArrayList<>(activeVehicles.values());
    }
}
