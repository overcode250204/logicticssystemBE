package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements BaseErrorCode {
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, Messages.UNAUTHENTICATED),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, Messages.UNAUTHORIZED);

    private final String message;
    private final HttpStatus httpStatus;

    AuthErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String UNAUTHENTICATED = "Unauthenticated";
        public static final String UNAUTHORIZED = "You do not have permission";
    }
}
