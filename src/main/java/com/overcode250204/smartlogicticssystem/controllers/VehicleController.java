package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.VehicleCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.VehicleUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.VehicleResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IVehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController extends BaseController {

    private final IVehicleService vehicleService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<VehicleResponseDTO>>> getAllVehicles(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(vehicleService.getAllVehicles(roleId, userId), "Vehicles retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<VehicleResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(vehicleService.getById(id, roleId, userId), "Vehicle retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<VehicleResponseDTO>> create(@Valid @RequestBody VehicleCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(vehicleService.create(request, roleId, userId), "Vehicle created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<VehicleResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody VehicleUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(vehicleService.update(id, request, roleId, userId), "Vehicle updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        vehicleService.delete(id, roleId, userId);
        return success(null, "Vehicle deleted successfully");
    }
}
