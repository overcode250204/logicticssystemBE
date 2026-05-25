package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryExportRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryBatchResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryExportResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-batches")
@Slf4j
@RequiredArgsConstructor
public class InventoryBatchController extends BaseController {

    private final IInventoryBatchService batchService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<InventoryBatchResponseDTO>>> getAllBatches() {
        return success(batchService.getAllBatches(), "All inventory batches retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<InventoryBatchResponseDTO>> create(
            @Valid @RequestBody InventoryBatchCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(batchService.create(request, roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InventoryBatchResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(batchService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<InventoryBatchResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody InventoryBatchUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(batchService.update(id, request, roleId, userId), "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        batchService.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }

    @GetMapping("/search/product")
    public ResponseEntity<BaseResponse<List<InventoryBatchResponseDTO>>> getBatchesByProductName(@RequestParam String name) {
        return success(batchService.getBatchesByProductName(name), "Inventory batches retrieved by product name");
    }

    @GetMapping("/search/supplier")
    public ResponseEntity<BaseResponse<List<InventoryBatchResponseDTO>>> getBatchesBySupplierName(@RequestParam String name) {
        return success(batchService.getBatchesBySupplierName(name), "Inventory batches retrieved by supplier name");
    }

    @PostMapping("/export")
    public ResponseEntity<BaseResponse<InventoryExportResponseDTO>> exportStock(@Valid @RequestBody InventoryExportRequest request) {
        return success(batchService.exportStock(request), "Stock exported successfully");
    }
}
