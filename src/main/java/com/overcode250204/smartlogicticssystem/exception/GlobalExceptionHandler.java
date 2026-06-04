package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Void>> handleAppException(AppException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(BaseResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred: " + e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse<Void>> handleMaxUploadSizeExceededException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(),
                        "Receipt image must be less than or equal to 5MB"));
    }
  
    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleS3Exception(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }
}
