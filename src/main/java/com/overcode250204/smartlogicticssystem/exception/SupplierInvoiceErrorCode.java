package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SupplierInvoiceErrorCode implements BaseErrorCode {
    SUPPLIER_INVOICE_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.SUPPLIER_INVOICE_NOT_FOUND),
    SUPPLIER_INVOICE_CODE_EXISTS(HttpStatus.BAD_REQUEST, Messages.SUPPLIER_INVOICE_CODE_EXISTS),
    DUE_DATE_BEFORE_INVOICE_DATE(HttpStatus.BAD_REQUEST, Messages.DUE_DATE_BEFORE_INVOICE_DATE),
    TOTAL_AMOUNT_LESS_THAN_PAID_AMOUNT(HttpStatus.BAD_REQUEST, Messages.TOTAL_AMOUNT_LESS_THAN_PAID_AMOUNT),
    PAYMENT_EXCEEDS_REMAINING_AMOUNT(HttpStatus.BAD_REQUEST, Messages.PAYMENT_EXCEEDS_REMAINING_AMOUNT);

    private final HttpStatus httpStatus;
    private final String message;

    SupplierInvoiceErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static class Messages {
        public static final String SUPPLIER_INVOICE_NOT_FOUND = "Supplier invoice not found";
        public static final String SUPPLIER_INVOICE_CODE_EXISTS = "Supplier invoice code already exists";
        public static final String DUE_DATE_BEFORE_INVOICE_DATE = "Due date cannot be before invoice date";
        public static final String TOTAL_AMOUNT_LESS_THAN_PAID_AMOUNT = "Total amount cannot be less than paid amount";
        public static final String PAYMENT_EXCEEDS_REMAINING_AMOUNT = "Payment amount cannot exceed remaining amount";

        public static final String INVOICE_CODE_REQUIRED = "Invoice code is required";
        public static final String INVOICE_CODE_TOO_LONG = "Invoice code must be less than 50 characters";
        public static final String SUPPLIER_ID_REQUIRED = "Supplier ID is required";
        public static final String INVOICE_DATE_REQUIRED = "Invoice date is required";
        public static final String DUE_DATE_REQUIRED = "Due date is required";
        public static final String TOTAL_AMOUNT_REQUIRED = "Total amount is required";
        public static final String TOTAL_AMOUNT_POSITIVE = "Total amount must be greater than 0";
        public static final String PAYMENT_AMOUNT_REQUIRED = "Payment amount is required";
        public static final String PAYMENT_AMOUNT_POSITIVE = "Payment amount must be greater than 0";
        public static final String PAYMENT_DATE_REQUIRED = "Payment date is required";
        public static final String PAYMENT_METHOD_REQUIRED = "Payment method is required";
    }
}
