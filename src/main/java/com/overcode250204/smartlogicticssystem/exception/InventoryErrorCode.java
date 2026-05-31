package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InventoryErrorCode implements BaseErrorCode {
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, Messages.INSUFFICIENT_STOCK),
    BATCH_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.BATCH_NOT_FOUND),
    BARCODE_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.BARCODE_NOT_FOUND),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.TRANSACTION_NOT_FOUND),
    TRANSACTION_RECORD_FAILED(HttpStatus.BAD_REQUEST, Messages.TRANSACTION_RECORD_FAILED);

    private final String message;
    private final HttpStatus httpStatus;

    InventoryErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String INSUFFICIENT_STOCK = "Insufficient stock in inventory";
        public static final String BATCH_NOT_FOUND = "Inventory batch not found";
        public static final String BARCODE_NOT_FOUND = "Inventory batch barcode not found";
        public static final String TRANSACTION_NOT_FOUND = "Inventory transaction not found";
        public static final String TRANSACTION_RECORD_FAILED = "Transaction record failed";
    }
}
