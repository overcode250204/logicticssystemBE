package com.overcode250204.smartlogicticssystem.controllers;


import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.order.OrderRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.shift.ShiftRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.shift.ShiftUpdateStatusRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.order.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.shift.ShiftResponseDTO;
import com.overcode250204.smartlogicticssystem.services.impls.DriverShiftService;
import com.overcode250204.smartlogicticssystem.services.impls.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController extends BaseController {
    private final DriverShiftService driverShiftService;

    @PostMapping
    public ResponseEntity<BaseResponse<ShiftResponseDTO>> create(@Valid @RequestBody ShiftRequest request,
                                                                 @RequestHeader(name = "X-Role-Id") int roleId,
                                                                 @RequestHeader(name = "X-User-Id") int userId) {
        return success(driverShiftService.create(request,roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ShiftResponseDTO>> getById(@PathVariable Long id,
                                                                  @RequestHeader(name = "X-Role-Id") int roleId,
                                                                  @RequestHeader(name = "X-User-Id") int userId) {
        return success(driverShiftService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ShiftResponseDTO>> updateWorkStatus(@PathVariable Long id,
                                                                 @Valid @RequestBody ShiftUpdateStatusRequest request,
                                                                 @RequestHeader(name = "X-Role-Id") int roleId,
                                                                 @RequestHeader(name = "X-User-Id") int userId) {
        return success(driverShiftService.updateWorkStatus(id, request, roleId, userId), "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                     @RequestHeader(name = "X-User-Id") int userId) {
        driverShiftService.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }
    @GetMapping()
    public ResponseEntity<BaseResponse<List<ShiftResponseDTO>>> getAll(
                                                                       @RequestHeader(name = "X-Role-Id") int roleId,
                                                                       @RequestHeader(name = "X-User-Id") int userId) {
        return success(driverShiftService.getAll( roleId, userId), "Get by id successfully");
    }

}
