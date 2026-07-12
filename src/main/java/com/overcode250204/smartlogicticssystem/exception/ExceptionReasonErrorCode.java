package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionReasonErrorCode implements BaseErrorCode {
    EXCEPTION_REASON_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.EXCEPTION_REASON_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    ExceptionReasonErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String EXCEPTION_REASON_NOT_FOUND = "Exception reason not found";
    }
}
