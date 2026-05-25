package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.SupplierInvoicePaymentCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierInvoicePaymentResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.SupplierInvoicePayment;
import org.springframework.stereotype.Component;

@Component
public class SupplierInvoicePaymentMapper {

    public SupplierInvoicePaymentResponseDTO toResponse(SupplierInvoicePayment payment) {
        if (payment == null) {
            return null;
        }

        return SupplierInvoicePaymentResponseDTO.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoice() != null ? payment.getInvoice().getId() : null)
                .invoiceCode(payment.getInvoice() != null ? payment.getInvoice().getInvoiceCode() : null)
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .note(payment.getNote())
                .createdById(payment.getCreatedBy() != null ? payment.getCreatedBy().getUserId() : null)
                .createdByName(payment.getCreatedBy() != null ? payment.getCreatedBy().getFullName() : null)
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public SupplierInvoicePayment toEntity(SupplierInvoicePaymentCreateRequest request) {
        if (request == null) {
            return null;
        }

        SupplierInvoicePayment payment = new SupplierInvoicePayment();
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setNote(request.getNote());
        return payment;
    }
}
