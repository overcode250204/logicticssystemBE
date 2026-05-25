package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InvoiceUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InvoiceResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceMapper {

    private final UserMapper userMapper;

    public InvoiceResponseDTO toResponse(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        return InvoiceResponseDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .invoiceType(invoice.getInvoiceType())
                .totalAmount(invoice.getTotalAmount())
                .createdBy(userMapper.toDTO(invoice.getCreatedBy()))
                .createdAt(invoice.getCreatedAt())
                .build();
    }

    public Invoice toEntity(InvoiceCreateRequest request) {
        if (request == null) {
            return null;
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setTotalAmount(request.getTotalAmount());
        return invoice;
    }

    public void updateEntity(InvoiceUpdateRequest request, Invoice invoice) {
        if (request == null || invoice == null) {
            return;
        }

        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setTotalAmount(request.getTotalAmount());
    }
}
