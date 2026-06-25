package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UnitErrorCode implements BaseErrorCode {
    UNIT_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.UNIT_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    UnitErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static class Messages {
        public static final String UNIT_NOT_FOUND = "Unit not found";
    }
}
