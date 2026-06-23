package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.RouteConfigResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IRouteConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route-configs")
@RequiredArgsConstructor
public class RouteConfigController extends BaseController {

    private final IRouteConfigService routeConfigService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<RouteConfigResponseDTO>>> getAllRouteConfigs(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(routeConfigService.getAllRouteConfigs(roleId, userId), "Route configs retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RouteConfigResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(routeConfigService.getById(id, roleId, userId), "Route config retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<RouteConfigResponseDTO>> create(@Valid @RequestBody RouteConfigCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(routeConfigService.create(request, roleId, userId), "Route config created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<RouteConfigResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody RouteConfigUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(routeConfigService.update(id, request, roleId, userId), "Route config updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        routeConfigService.delete(id, roleId, userId);
        return success(null, "Route config deleted successfully");
    }
}
