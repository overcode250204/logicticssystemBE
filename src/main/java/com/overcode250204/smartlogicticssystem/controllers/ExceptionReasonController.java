package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ExceptionReasonResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IExceptionReasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/exception-reasons")
@RequiredArgsConstructor
public class ExceptionReasonController extends BaseController {

    private final IExceptionReasonService exceptionReasonService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ExceptionReasonResponseDTO>>> getAll() {
        return success(exceptionReasonService.getAllExceptionReasons(), "All exception reasons retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ExceptionReasonResponseDTO>> create(
            @Valid @RequestBody ExceptionReasonCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(exceptionReasonService.create(request, roleId, userId), "Exception reason created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ExceptionReasonResponseDTO>> getById(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(exceptionReasonService.getById(id, roleId, userId), "Exception reason retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ExceptionReasonResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExceptionReasonUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(exceptionReasonService.update(id, request, roleId, userId), "Exception reason updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        exceptionReasonService.delete(id, roleId, userId);
        return success(null, "Exception reason deleted successfully");
    }
}
