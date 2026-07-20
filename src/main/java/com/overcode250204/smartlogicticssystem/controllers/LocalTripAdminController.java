package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.LocalTripResponseDTO;
import com.overcode250204.smartlogicticssystem.services.ILocalTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/local-trips")
@RequiredArgsConstructor
public class LocalTripAdminController extends BaseController {

    private final ILocalTripService localTripService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<LocalTripResponseDTO>>> getAllLocalTrips(
            @RequestParam(required = false) com.overcode250204.smartlogicticssystem.enums.LocalTripStatus status) {
        return success(localTripService.getAllLocalTrips(status), "All local trips retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LocalTripResponseDTO>> getLocalTripById(@PathVariable Long id) {
        return success(localTripService.getLocalTripById(id), "Local trip retrieved successfully");
    }

    @PostMapping("/plan")
    public ResponseEntity<BaseResponse<List<LocalTripResponseDTO>>> planLocalTrips() {
        return success(localTripService.planLocalTrips(), "Local trips planned successfully");
    }

    @PutMapping("/{tripId}/change-vehicle/{newVehicleId}")
    public ResponseEntity<BaseResponse<Void>> changeVehicle(@PathVariable Long tripId, @PathVariable Long newVehicleId) {
        localTripService.changeVehicle(tripId, newVehicleId);
        return success(null, "Vehicle changed successfully");
    }

    @PutMapping("/{tripId}/change-driver/{driverId}")
    public ResponseEntity<BaseResponse<Void>> changeDriver(@PathVariable Long tripId, @PathVariable Long driverId) {
        localTripService.changeDriver(tripId, driverId);
        return success(null, "Driver changed successfully");
    }

    @PutMapping("/{cancelledTripId}/collapse/{targetTripId}")
    public ResponseEntity<BaseResponse<Void>> collapseTrip(@PathVariable Long cancelledTripId, @PathVariable Long targetTripId) {
        localTripService.collapseTrip(cancelledTripId, targetTripId);
        return success(null, "Trips collapsed successfully");
    }
}
