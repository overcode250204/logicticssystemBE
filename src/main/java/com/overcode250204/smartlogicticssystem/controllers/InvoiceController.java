package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.DashboardResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InvoiceResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@Slf4j
@RequiredArgsConstructor
public class InvoiceController extends BaseController {

    private final IInvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<InvoiceResponseDTO>>> getAllInvoices() {
        return success(invoiceService.getAllInvoices(), "All invoices retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<InvoiceResponseDTO>> create(@Valid @RequestBody InvoiceCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(invoiceService.create(request, roleId, userId), "Invoice created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InvoiceResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(invoiceService.getById(id, roleId, userId), "Invoice retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<InvoiceResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody InvoiceUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(invoiceService.update(id, request, roleId, userId), "Invoice updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        invoiceService.delete(id, roleId, userId);
        return success(null, "Invoice deleted successfully");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<BaseResponse<DashboardResponseDTO>> getDashboard() {
        return success(invoiceService.getDashboardStats(), "Dashboard statistics retrieved successfully");
    }
}
