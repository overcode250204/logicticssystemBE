package com.overcode250204.smartlogicticssystem.controllers;


import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.ZoneCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ZoneResponseDTO;
import com.overcode250204.smartlogicticssystem.services.impls.ZoneServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController extends BaseController {
    private final ZoneServiceImpl zoneService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ZoneResponseDTO>>> getAll(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(zoneService.getAll(roleId, userId), "Zones retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ZoneResponseDTO>> getById(@PathVariable Long id,
                                                                      @RequestHeader(name = "X-Role-Id") int roleId,
                                                                      @RequestHeader(name = "X-User-Id") int userId) {
        return success(zoneService.getById(id, roleId, userId), "Warehouse retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ZoneResponseDTO>> create(@Valid @RequestBody ZoneCreateRequest request,
                                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                                     @RequestHeader(name = "X-User-Id") int userId) {
        return success(zoneService.create(request, roleId, userId), "Warehouse created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ZoneResponseDTO>> update(@PathVariable Long id,
                                                                     @Valid @RequestBody ZoneCreateRequest request,
                                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                                     @RequestHeader(name = "X-User-Id") int userId) {
        return success(zoneService.update(id, request, roleId, userId), "Warehouse updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                     @RequestHeader(name = "X-User-Id") int userId) {
        zoneService.delete(id, roleId, userId);
        return success(null, "Warehouse deleted successfully");
    }

}