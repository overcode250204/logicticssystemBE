package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionRejectRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import com.overcode250204.smartlogicticssystem.services.IFinanceTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/finance-transactions")
@RequiredArgsConstructor
public class FinanceTransactionController extends BaseController {
    private final IFinanceTransactionService financeTransactionService;

    @PostMapping
    public ResponseEntity<BaseResponse<FinanceTransactionResponseDTO>> create(
            @Valid @RequestBody FinanceTransactionCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(financeTransactionService.create(request, roleId, userId),
                "Finance transaction created successfully");
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<FinanceTransactionResponseDTO>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) FinanceTransactionType type,
            @RequestParam(required = false) FinanceTransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return success(financeTransactionService.search(keyword, type, status, fromDate, toDate, page, size),
                "Finance transactions retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<FinanceTransactionResponseDTO>> getById(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(financeTransactionService.getById(id, roleId, userId),
                "Finance transaction retrieved successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        financeTransactionService.delete(id, roleId, userId);
        return success(null, "Finance transaction deleted successfully");
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BaseResponse<FinanceTransactionResponseDTO>> approve(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(financeTransactionService.approve(id, roleId, userId),
                "Finance transaction approved successfully");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<BaseResponse<FinanceTransactionResponseDTO>> reject(
            @PathVariable Long id,
            @Valid @RequestBody FinanceTransactionRejectRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(financeTransactionService.reject(id, request, roleId, userId),
                "Finance transaction rejected successfully");
    }
}
