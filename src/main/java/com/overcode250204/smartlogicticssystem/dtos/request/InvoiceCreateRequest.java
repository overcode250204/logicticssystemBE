package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.exception.InvoiceErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceCreateRequest {
    @NotBlank(message = InvoiceErrorCode.Messages.INVOICE_TYPE_REQUIRED)
    private String invoiceType;

    @NotNull(message = InvoiceErrorCode.Messages.TOTAL_AMOUNT_REQUIRED)
    @Positive(message = InvoiceErrorCode.Messages.TOTAL_AMOUNT_POSITIVE)
    private BigDecimal totalAmount;

    @NotNull(message = InvoiceErrorCode.Messages.CREATED_BY_REQUIRED)
    private Long createdById;
}
