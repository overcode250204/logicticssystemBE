package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final IErrorCode errorCode;

    public AppException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
