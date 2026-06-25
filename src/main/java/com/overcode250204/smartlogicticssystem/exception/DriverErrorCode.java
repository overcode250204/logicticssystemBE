package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DriverErrorCode implements BaseErrorCode {
    DRIVER_NOT_FOUND(HttpStatus.NOT_FOUND, DriverErrorCode.Messages.DRIVER_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    DriverErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String DRIVER_NOT_FOUND = "Driver not found";
    }
}
