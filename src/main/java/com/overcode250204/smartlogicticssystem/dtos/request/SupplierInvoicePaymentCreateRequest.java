package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.PaymentMethod;
import com.overcode250204.smartlogicticssystem.exception.SupplierInvoiceErrorCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SupplierInvoicePaymentCreateRequest {
    @NotNull(message = SupplierInvoiceErrorCode.Messages.PAYMENT_AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = SupplierInvoiceErrorCode.Messages.PAYMENT_AMOUNT_POSITIVE)
    private BigDecimal amount;

    @NotNull(message = SupplierInvoiceErrorCode.Messages.PAYMENT_DATE_REQUIRED)
    private LocalDate paymentDate;

    @NotNull(message = SupplierInvoiceErrorCode.Messages.PAYMENT_METHOD_REQUIRED)
    private PaymentMethod paymentMethod;

    private String note;
}
