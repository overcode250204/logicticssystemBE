package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderErrorCode implements BaseErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.ORDER_NOT_FOUND),
    DELIVERY_PROVINCE_UNSUPPORTED(HttpStatus.BAD_REQUEST, Messages.DELIVERY_PROVINCE_UNSUPPORTED),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    OrderErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String ORDER_NOT_FOUND = "Order not found";
        public static final String DELIVERY_PROVINCE_UNSUPPORTED = "Delivery to this province is currently not supported";
        public static final String PRODUCT_NOT_FOUND = "Product not found";

    }
}
