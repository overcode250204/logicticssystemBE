package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoicePaymentCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoicePaymentResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoiceResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.InvoiceStatus;
import com.overcode250204.smartlogicticssystem.services.ISupplierInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/supplier-invoices")
@RequiredArgsConstructor
public class SupplierInvoiceController extends BaseController {
    private final ISupplierInvoiceService supplierInvoiceService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<SupplierInvoiceResponseDTO>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return success(supplierInvoiceService.search(keyword, supplierId, status, fromDate, toDate, page, size),
                "Supplier invoices retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<SupplierInvoiceResponseDTO>> create(
            @Valid @RequestBody SupplierInvoiceCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(supplierInvoiceService.create(request, roleId, userId),
                "Supplier invoice created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SupplierInvoiceResponseDTO>> getById(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(supplierInvoiceService.getById(id, roleId, userId),
                "Supplier invoice retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<SupplierInvoiceResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody SupplierInvoiceUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(supplierInvoiceService.update(id, request, roleId, userId),
                "Supplier invoice updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        supplierInvoiceService.delete(id, roleId, userId);
        return success(null, "Supplier invoice deleted successfully");
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<BaseResponse<SupplierInvoicePaymentResponseDTO>> addPayment(
            @PathVariable Long id,
            @Valid @RequestBody SupplierInvoicePaymentCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId
    ) {
        return success(supplierInvoiceService.addPayment(id, request, roleId, userId),
                "Supplier invoice payment created successfully");
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<BaseResponse<List<SupplierInvoicePaymentResponseDTO>>> getPayments(@PathVariable Long id) {
        return success(supplierInvoiceService.getPayments(id),
                "Supplier invoice payments retrieved successfully");
    }
}
