package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements IErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_EXISTED(HttpStatus.BAD_REQUEST, "User existed"),
    USERNAME_INVALID(HttpStatus.BAD_REQUEST, "Username must be at least 3 characters"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters"),
    INVALID_DOB(HttpStatus.BAD_REQUEST, "Your age must be at least {min}"),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    UserErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }
}
