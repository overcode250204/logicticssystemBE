package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProductErrorCode implements IErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    ProductErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }
}
