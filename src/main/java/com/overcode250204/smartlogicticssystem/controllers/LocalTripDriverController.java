package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.services.ILocalTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver/local-trips")
@RequiredArgsConstructor
public class LocalTripDriverController {

    private final ILocalTripService localTripService;

    @PutMapping("/{tripId}/accept")
    public ResponseEntity<Void> acceptTrip(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId) {
        localTripService.acceptTrip(driverId, tripId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{tripId}/cancel")
    public ResponseEntity<Void> cancelTrip(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId) {
        localTripService.cancelTrip(driverId, tripId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripId}/scan-barcode")
    public ResponseEntity<Void> scanBarcode(@PathVariable Long tripId, @RequestParam Long orderId, @RequestParam String barcode) {
        localTripService.scanBarcode(tripId, orderId, barcode);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{tripId}/start-executing")
    public ResponseEntity<Void> startExecuting(@RequestHeader("driverId") Long driverId, @PathVariable Long tripId) {
        localTripService.startExecuting(driverId, tripId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{tripId}/details/{detailId}/arrive")
    public ResponseEntity<Void> arriveAtPoint(@RequestHeader("driverId") Long driverId, @PathVariable Long detailId,
                                              @RequestParam double lat, @RequestParam double lon) {
        localTripService.arriveAtPoint(driverId, detailId, lat, lon);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{tripId}/details/{detailId}/complete")
    public ResponseEntity<Void> completePoint(@RequestHeader("driverId") Long driverId, @PathVariable Long detailId,
                                              @RequestParam String proofUrl) {
        localTripService.completePoint(driverId, detailId, proofUrl);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{tripId}/details/{detailId}/fail")
    public ResponseEntity<Void> failPoint(@RequestHeader("driverId") Long driverId, @PathVariable Long detailId,
                                          @RequestParam String proofUrl) {
        localTripService.failPoint(driverId, detailId, proofUrl);
        return ResponseEntity.ok().build();
    }
}
