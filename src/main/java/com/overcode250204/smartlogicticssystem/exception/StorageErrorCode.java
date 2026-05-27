package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StorageErrorCode implements BaseErrorCode {
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, Messages.FILE_UPLOAD_FAILED),
    FILE_DELETE_FAILED(HttpStatus.BAD_REQUEST, Messages.FILE_DELETE_FAILED),
    INVALID_FILE(HttpStatus.BAD_REQUEST, Messages.INVALID_FILE),
    QR_CODE_GENERATION_FAILED(HttpStatus.BAD_REQUEST, Messages.QR_CODE_GENERATION_FAILED);

    private final String message;
    private final HttpStatus httpStatus;

    StorageErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static class Messages {
        public static final String FILE_UPLOAD_FAILED = "File upload failed";
        public static final String FILE_DELETE_FAILED = "File delete failed";
        public static final String INVALID_FILE = "Invalid file";
        public static final String QR_CODE_GENERATION_FAILED = "QR code generation failed";
    }
}
