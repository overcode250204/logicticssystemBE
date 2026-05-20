package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InventoryErrorCode implements IErrorCode {
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock in inventory"),
    BATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "Inventory batch not found"),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Inventory transaction not found"),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    InventoryErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }
}
