package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InventoryBatchErrorCode implements IErrorCode {
    BATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "Inventory batch not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found for this batch"),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    InventoryBatchErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }
}
