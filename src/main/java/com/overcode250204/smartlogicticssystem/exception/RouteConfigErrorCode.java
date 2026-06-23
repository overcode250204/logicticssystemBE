package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RouteConfigErrorCode implements BaseErrorCode {
    ROUTE_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.ROUTE_CONFIG_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    RouteConfigErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String ROUTE_CONFIG_NOT_FOUND = "Route config not found";
    }
}
