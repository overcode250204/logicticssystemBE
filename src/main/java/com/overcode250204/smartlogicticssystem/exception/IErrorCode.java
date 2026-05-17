package com.overcode250204.smartlogicticssystem.exception;

import org.springframework.http.HttpStatus;

public interface IErrorCode {
    default int getCode() {
        return getHttpStatus().value();
    }

    String getMessage();

    HttpStatus getHttpStatus();
}
