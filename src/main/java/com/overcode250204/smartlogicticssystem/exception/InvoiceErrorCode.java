package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InvoiceErrorCode implements BaseErrorCode {
    INVOICE_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.INVOICE_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    InvoiceErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String INVOICE_NOT_FOUND = "Invoice not found";

        public static final String INVOICE_TYPE_REQUIRED = "Invoice type is required";
        public static final String TOTAL_AMOUNT_REQUIRED = "Total amount is required";
        public static final String TOTAL_AMOUNT_POSITIVE = "Total amount must be positive";
        public static final String CREATED_BY_REQUIRED = "Creator user ID is required";
    }
}
