package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.entities.LocalTrip;
import com.overcode250204.smartlogicticssystem.services.ILocalTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/local-trips")
@RequiredArgsConstructor
public class LocalTripAdminController {

    private final ILocalTripService localTripService;

    @PostMapping("/plan")
    public ResponseEntity<List<LocalTrip>> planLocalTrips(@RequestParam Long zoneId, @RequestBody List<Long> orderIds) {
        return ResponseEntity.ok(localTripService.planLocalTrips(zoneId, orderIds));
    }

    @PutMapping("/{tripId}/change-vehicle/{newVehicleId}")
    public ResponseEntity<Void> changeVehicle(@PathVariable Long tripId, @PathVariable Long newVehicleId) {
        localTripService.changeVehicle(tripId, newVehicleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{cancelledTripId}/collapse/{targetTripId}")
    public ResponseEntity<Void> collapseTrip(@PathVariable Long cancelledTripId, @PathVariable Long targetTripId) {
        localTripService.collapseTrip(cancelledTripId, targetTripId);
        return ResponseEntity.ok().build();
    }
}
