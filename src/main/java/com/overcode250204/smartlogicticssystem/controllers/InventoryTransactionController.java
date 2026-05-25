package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
@Slf4j
@RequiredArgsConstructor
public class InventoryTransactionController extends BaseController {

    private final IInventoryTransactionService transactionService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<InventoryTransactionResponseDTO>>> getAllTransactions() {
        return success(transactionService.getAllTransactions(), "All inventory transactions retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<InventoryTransactionResponseDTO>> create(
            @Valid @RequestBody InventoryTransactionCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(transactionService.create(request, roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InventoryTransactionResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(transactionService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<InventoryTransactionResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody InventoryTransactionUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(transactionService.update(id, request, roleId, userId), "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        transactionService.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }
}
