package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum WarehouseErrorCode implements BaseErrorCode {
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.WAREHOUSE_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    WarehouseErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String WAREHOUSE_NOT_FOUND = "Warehouse not found";
    }
}
