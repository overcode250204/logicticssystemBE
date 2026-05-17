package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppErrorCode implements IErrorCode {
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Uncategorized error"),
    INVALID_KEY(HttpStatus.BAD_REQUEST, "Invalid message key"),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    AppErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }
}
