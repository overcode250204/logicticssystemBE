package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.exception.SupplierInvoiceErrorCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SupplierInvoiceUpdateRequest {
    @NotBlank(message = SupplierInvoiceErrorCode.Messages.INVOICE_CODE_REQUIRED)
    @Size(max = 50, message = SupplierInvoiceErrorCode.Messages.INVOICE_CODE_TOO_LONG)
    private String invoiceCode;

    @NotNull(message = SupplierInvoiceErrorCode.Messages.SUPPLIER_ID_REQUIRED)
    private Integer supplierId;

    @NotNull(message = SupplierInvoiceErrorCode.Messages.INVOICE_DATE_REQUIRED)
    private LocalDate invoiceDate;

    @NotNull(message = SupplierInvoiceErrorCode.Messages.DUE_DATE_REQUIRED)
    private LocalDate dueDate;

    @NotNull(message = SupplierInvoiceErrorCode.Messages.TOTAL_AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = SupplierInvoiceErrorCode.Messages.TOTAL_AMOUNT_POSITIVE)
    private BigDecimal totalAmount;

    private String note;
}
