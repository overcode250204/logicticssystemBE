package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FileStorageErrorCode implements BaseErrorCode {
    FILE_REQUIRED(HttpStatus.BAD_REQUEST, Messages.FILE_REQUIRED),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, Messages.INVALID_FILE_TYPE),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, Messages.FILE_TOO_LARGE),
    BUCKET_NOT_CONFIGURED(HttpStatus.INTERNAL_SERVER_ERROR, Messages.BUCKET_NOT_CONFIGURED),
    S3_BUCKET_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, Messages.S3_BUCKET_NOT_FOUND),
    S3_ACCESS_DENIED(HttpStatus.INTERNAL_SERVER_ERROR, Messages.S3_ACCESS_DENIED),
    S3_CONFIGURATION_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, Messages.S3_CONFIGURATION_INVALID),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, Messages.FILE_UPLOAD_FAILED),
    PRESIGNED_URL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, Messages.PRESIGNED_URL_FAILED);

    private final HttpStatus httpStatus;
    private final String message;

    FileStorageErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static class Messages {
        public static final String FILE_REQUIRED = "Receipt image is required";
        public static final String INVALID_FILE_TYPE = "Only jpeg, png and webp images are allowed";
        public static final String FILE_TOO_LARGE = "Receipt image must be less than or equal to 5MB";
        public static final String BUCKET_NOT_CONFIGURED = "Receipt S3 bucket is not configured";
        public static final String S3_BUCKET_NOT_FOUND = "Receipt S3 bucket was not found";
        public static final String S3_ACCESS_DENIED = "AWS credentials do not have permission to upload receipt image";
        public static final String S3_CONFIGURATION_INVALID = "AWS S3 configuration is invalid or credentials cannot be loaded";
        public static final String FILE_UPLOAD_FAILED = "Failed to upload receipt image";
        public static final String PRESIGNED_URL_FAILED = "Failed to create presigned URL";
    }
}
