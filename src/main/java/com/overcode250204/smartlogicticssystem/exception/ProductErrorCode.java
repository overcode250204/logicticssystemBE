package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProductErrorCode implements BaseErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, Messages.INVALID_QUANTITY),
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, Messages.NOT_ENOUGH_STOCK),
    PRODUCT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Messages.PRODUCT_ALREADY_EXISTS);

    private final String message;
    private final HttpStatus httpStatus;

    ProductErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String PRODUCT_NOT_FOUND = "Product not found";
        public static final String INVALID_QUANTITY = "Quantity must be greater than 0";
        public static final String NOT_ENOUGH_STOCK = "Not enough stock";
        public static final String PRODUCT_ALREADY_EXISTS = "Product code already exists";

        // DTO field validation messages
        public static final String PRODUCT_CODE_REQUIRED = "Product code is required";
        public static final String PRODUCT_CODE_TOO_LONG = "Product code must be less than 50 characters";
        public static final String PRODUCT_NAME_REQUIRED = "Product name is required";
        public static final String PRODUCT_NAME_TOO_LONG = "Product name must be less than 150 characters";
        public static final String PRICE_REQUIRED = "Price is required";
        public static final String PRICE_POSITIVE = "Price must be positive";
        public static final String SUPPLIER_ID_REQUIRED = "Supplier ID is required";
    }
}
