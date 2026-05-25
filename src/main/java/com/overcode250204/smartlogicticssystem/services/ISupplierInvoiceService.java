package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoicePaymentCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoicePaymentResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoiceResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.InvoiceStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ISupplierInvoiceService {
    Page<SupplierInvoiceResponseDTO> search(String keyword, Integer supplierId, InvoiceStatus status,
                                            LocalDate fromDate, LocalDate toDate, int page, int size);

    SupplierInvoiceResponseDTO create(SupplierInvoiceCreateRequest request, int roleId, int userId);

    SupplierInvoiceResponseDTO update(Long id, SupplierInvoiceUpdateRequest request, int roleId, int userId);

    SupplierInvoiceResponseDTO getById(Long id, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    SupplierInvoicePaymentResponseDTO addPayment(Long invoiceId, SupplierInvoicePaymentCreateRequest request,
                                                 int roleId, int userId);

    List<SupplierInvoicePaymentResponseDTO> getPayments(Long invoiceId);
}
