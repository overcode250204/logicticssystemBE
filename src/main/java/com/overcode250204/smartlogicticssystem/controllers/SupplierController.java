package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierResponseDTO;
import com.overcode250204.smartlogicticssystem.services.ISupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Slf4j
@RequiredArgsConstructor
public class SupplierController extends BaseController {

    private final ISupplierService supplierService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<SupplierResponseDTO>>> getAllSuppliers() {
        return success(supplierService.getAllSuppliers(), "All suppliers retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<SupplierResponseDTO>> create(@Valid @RequestBody SupplierCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(supplierService.create(request, roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SupplierResponseDTO>> getById(@PathVariable Integer id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(supplierService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<SupplierResponseDTO>> update(@PathVariable Integer id,
            @Valid @RequestBody SupplierUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(supplierService.update(id, request, roleId, userId), "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Integer id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        supplierService.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }
}
