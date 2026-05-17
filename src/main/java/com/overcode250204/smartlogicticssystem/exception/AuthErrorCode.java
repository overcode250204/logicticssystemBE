package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements IErrorCode {
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Unauthenticated"),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "You do not have permission"),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    AuthErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }
}
