package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.PaymentMethod;
import com.overcode250204.smartlogicticssystem.exception.FinanceTransactionErrorCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FinanceTransactionUpdateRequest {
    @NotNull(message = FinanceTransactionErrorCode.Messages.AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = FinanceTransactionErrorCode.Messages.AMOUNT_POSITIVE)
    private BigDecimal amount;

    @NotNull(message = FinanceTransactionErrorCode.Messages.TRANSACTION_DATE_REQUIRED)
    private LocalDate transactionDate;

    @NotNull(message = FinanceTransactionErrorCode.Messages.PAYMENT_METHOD_REQUIRED)
    private PaymentMethod paymentMethod;

    @Size(max = 500, message = FinanceTransactionErrorCode.Messages.DESCRIPTION_TOO_LONG)
    private String description;

    @Size(max = 500, message = FinanceTransactionErrorCode.Messages.RECEIPT_IMAGE_KEY_TOO_LONG)
    private String receiptImageKey;
}
