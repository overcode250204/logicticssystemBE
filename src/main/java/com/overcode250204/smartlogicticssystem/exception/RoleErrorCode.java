package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RoleErrorCode implements IErrorCode {
    ROLE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.ROLE_NAME_NOT_FOUND),
    ROLE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.ROLE_ID_NOT_FOUND),
    ROLE_HAS_NO_PERMISSION(HttpStatus.FORBIDDEN, Messages.ROLE_HAS_NO_PERMISSION);

    private final String message;
    private final HttpStatus httpStatus;

    RoleErrorCode(HttpStatus httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static class Messages {
        public static final String ROLE_NAME_NOT_FOUND = "Role name not found";
        public static final String ROLE_ID_NOT_FOUND = "Role id not found";
        public static final String ROLE_HAS_NO_PERMISSION = "Role has no permission";
    }
}
