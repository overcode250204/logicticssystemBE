package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoiceResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.SupplierInvoice;
import org.springframework.stereotype.Component;

@Component
public class SupplierInvoiceMapper {

    public SupplierInvoiceResponseDTO toResponse(SupplierInvoice invoice) {
        if (invoice == null) {
            return null;
        }

        return SupplierInvoiceResponseDTO.builder()
                .id(invoice.getId())
                .invoiceCode(invoice.getInvoiceCode())
                .supplierId(invoice.getSupplier() != null ? invoice.getSupplier().getSupplierId() : null)
                .supplierName(invoice.getSupplier() != null ? invoice.getSupplier().getSupplierName() : null)
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .remainingAmount(invoice.getRemainingAmount())
                .status(invoice.getStatus())
                .note(invoice.getNote())
                .isActive(invoice.getIsActive())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

    public SupplierInvoice toEntity(SupplierInvoiceCreateRequest request) {
        if (request == null) {
            return null;
        }

        SupplierInvoice invoice = new SupplierInvoice();
        invoice.setInvoiceCode(request.getInvoiceCode());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setNote(request.getNote());
        return invoice;
    }

    public void updateEntity(SupplierInvoiceUpdateRequest request, SupplierInvoice invoice) {
        if (request == null || invoice == null) {
            return;
        }

        invoice.setInvoiceCode(request.getInvoiceCode());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setNote(request.getNote());
    }
}
