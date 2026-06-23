package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RouteProvinceErrorCode implements BaseErrorCode {
    ROUTE_PROVINCE_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.ROUTE_PROVINCE_NOT_FOUND),
    PROVINCE_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Messages.PROVINCE_NAME_ALREADY_EXISTS);

    private final String message;
    private final HttpStatus httpStatus;

    RouteProvinceErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String ROUTE_PROVINCE_NOT_FOUND = "Route province not found";
        public static final String PROVINCE_NAME_ALREADY_EXISTS = "Province name already exists";
    }
}
