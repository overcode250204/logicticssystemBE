package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SupplierErrorCode implements BaseErrorCode {
    SUPPLIER_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.SUPPLIER_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    SupplierErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String SUPPLIER_NOT_FOUND = "Supplier not found";
        public static final String SUPPLIER_NAME_REQUIRED = "Supplier name is required";
        public static final String SUPPLIER_NAME_TOO_LONG = "Supplier name must be less than 150 characters";
    }
}
