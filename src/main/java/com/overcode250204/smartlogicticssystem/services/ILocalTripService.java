package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.entities.LocalTrip;

import com.overcode250204.smartlogicticssystem.dtos.request.FailPointRequestDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.LocalTripResponseDTO;
import java.util.List;

public interface ILocalTripService {
    List<LocalTripResponseDTO> planLocalTrips();
    List<LocalTripResponseDTO> getAllLocalTrips();
    List<LocalTripResponseDTO> getLocalTripsByDriverId(Long driverId);
    LocalTripResponseDTO getLocalTripById(Long id);
    void acceptTrip(Long driverId, Long tripId);
    void cancelTrip(Long driverId, Long tripId);
    void collapseTrip(Long cancelledTripId, Long targetTripId);
    void changeVehicle(Long tripId, Long newVehicleId);
    void changeDriver(Long tripId, Long driverId);
    void scanBarcode(Long driverId, Long tripId, Long orderId, String barcode);
    void startExecuting(Long driverId, Long tripId);
    void arriveAtPoint(Long driverId, Long tripId, Long detailId, double lat, double lon);
    void completePoint(Long driverId, Long tripId, Long detailId, String proofUrl);
    void failPoint(Long driverId, Long tripId, Long detailId, String proofUrl, FailPointRequestDTO data);
}
