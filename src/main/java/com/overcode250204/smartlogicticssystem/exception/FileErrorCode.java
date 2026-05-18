package com.overcode250204.smartlogicticssystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FileErrorCode implements IErrorCode {

    EMPTY_FILE(
            HttpStatus.BAD_REQUEST,
        "File is empty"
    ),

    INVALID_CSV_FILE(
            HttpStatus.BAD_REQUEST,
        "Invalid CSV file"
    ),

    CSV_PARSE_ERROR(
            HttpStatus.BAD_REQUEST,
        "Failed to parse CSV file"
    ),

    FILE_PROCESSING_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
        "Failed to process file"
    ),

    FILE_UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
        "Failed to upload file"
    ),

    S3_UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
        "Failed to upload file to S3"
    ),

    S3_BUCKET_NOT_FOUND(
            HttpStatus.INTERNAL_SERVER_ERROR,
        "S3 bucket not found"
    ),

    S3_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
        "Access denied to S3 bucket"
    ),

    FILE_TOO_LARGE(
            HttpStatus.PAYLOAD_TOO_LARGE,
        "File size exceeds allowed limit"
    ),

    INVENTORY_IMPORT_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
        "Failed to import inventory data"
    );

    private final String message;
    private final HttpStatus httpStatus;

    FileErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }


}
