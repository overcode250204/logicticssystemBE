package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.WarehouseResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IWarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController extends BaseController {

    private final IWarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<WarehouseResponseDTO>>> getAllWarehouses(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(warehouseService.getAllWarehouses(roleId, userId), "Warehouses retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<WarehouseResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(warehouseService.getById(id, roleId, userId), "Warehouse retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<WarehouseResponseDTO>> create(@Valid @RequestBody WarehouseCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(warehouseService.create(request, roleId, userId), "Warehouse created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<WarehouseResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody WarehouseUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(warehouseService.update(id, request, roleId, userId), "Warehouse updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        warehouseService.delete(id, roleId, userId);
        return success(null, "Warehouse deleted successfully");
    }
}
