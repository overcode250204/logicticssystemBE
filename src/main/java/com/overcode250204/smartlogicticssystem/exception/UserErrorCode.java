package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.USER_NOT_FOUND),
    USER_EXISTED(HttpStatus.BAD_REQUEST, Messages.USER_EXISTED),
    USERNAME_INVALID(HttpStatus.BAD_REQUEST, Messages.USERNAME_INVALID),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, Messages.INVALID_PASSWORD),
    INVALID_DOB(HttpStatus.BAD_REQUEST, Messages.INVALID_DOB),
    USER_IS_INACTIVE(HttpStatus.BAD_REQUEST, Messages.USER_IS_INACTIVE);

    private final String message;
    private final HttpStatus httpStatus;

    UserErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String USER_NOT_FOUND = "User not found";
        public static final String USER_EXISTED = "User existed";
        public static final String USERNAME_INVALID = "Username must be at least 3 characters";
        public static final String INVALID_PASSWORD = "Password must be at least 8 characters";
        public static final String INVALID_DOB = "Your age must be at least {min}";
        public static final String USER_IS_INACTIVE = "User is inactive";
    }
}
