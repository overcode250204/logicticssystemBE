package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RoleErrorCode implements IErrorCode{
    ROLE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Role name not found"),
    ROLE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "Role id not found"),
    ROLE_HAS_NO_PERMISSION(HttpStatus.FORBIDDEN, "Role has no permission"),
    ERROR_DRIVER_ROLE(HttpStatus.FORBIDDEN, "Error driver role"),
    ;

    RoleErrorCode(HttpStatus httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final String message;
    private final HttpStatus httpStatus;

}
