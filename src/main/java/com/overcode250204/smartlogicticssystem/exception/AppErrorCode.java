package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppErrorCode implements IErrorCode {
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, Messages.UNCATEGORIZED_EXCEPTION),
    INVALID_KEY(HttpStatus.BAD_REQUEST, Messages.INVALID_KEY);

    private final String message;
    private final HttpStatus httpStatus;

    AppErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String UNCATEGORIZED_EXCEPTION = "Uncategorized error";
        public static final String INVALID_KEY = "Invalid message key";
    }
}
