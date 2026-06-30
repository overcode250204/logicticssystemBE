package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.entities.LocalTrip;

import java.util.List;

public interface ILocalTripService {
    List<LocalTrip> planLocalTrips(Long zoneId, List<Long> orderIds);
    void acceptTrip(Long driverId, Long tripId);
    void cancelTrip(Long driverId, Long tripId);
    void collapseTrip(Long cancelledTripId, Long targetTripId);
    void changeVehicle(Long tripId, Long newVehicleId);
    void scanBarcode(Long tripId, Long orderId, String barcode);
    void startExecuting(Long driverId, Long tripId);
    void arriveAtPoint(Long driverId, Long detailId, double lat, double lon);
    void completePoint(Long driverId, Long detailId, String proofUrl);
    void failPoint(Long driverId, Long detailId, String proofUrl);
}
