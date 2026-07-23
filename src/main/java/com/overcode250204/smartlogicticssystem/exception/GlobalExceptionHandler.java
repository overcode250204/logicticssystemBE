package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Void>> handleAppException(AppException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(BaseResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaseResponse<Void>> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(BaseResponse.error(e.getStatusCode().value(), e.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(Exception e) {
        log.error("Unhandled application exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred: " + e.getMessage()));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<BaseResponse<Void>> handleInvalidRequestParameter(Exception e) {
        String message;
        if (e instanceof MethodArgumentTypeMismatchException mismatch) {
            message = "Invalid value for request parameter '" + mismatch.getName() + "'";
        } else if (e instanceof MissingServletRequestParameterException missing) {
            message = "Missing required request parameter '" + missing.getParameterName() + "'";
        } else {
            message = "Request parameters are invalid";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException e
    ) {
        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(e);

        String message = "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc cơ sở dữ liệu.";

        if (rootCause instanceof PSQLException postgresException) {
            ServerErrorMessage serverError = postgresException.getServerErrorMessage();

            if (serverError != null) {
                String constraint = serverError.getConstraint();
                String table = serverError.getTable();
                String column = serverError.getColumn();

                log.warn(
                        "Database constraint violation. sqlState={}, table={}, column={}, constraint={}, detail={}",
                        postgresException.getSQLState(),
                        table,
                        column,
                        constraint,
                        serverError.getDetail()
                );

                // PostgreSQL SQLSTATE:
                // 23505 = unique violation
                // 23503 = foreign key violation
                // 23502 = not null violation
                // 23514 = check constraint violation
                switch (postgresException.getSQLState()) {
                    case "23505" -> message = buildUniqueMessage(column, constraint);
                    case "23503" -> message = buildForeignKeyMessage(column, constraint);
                    case "23502" -> message = buildNotNullMessage(column);
                    case "23514" -> message = buildCheckConstraintMessage(column, constraint);
                    default -> message = buildGeneralDatabaseMessage(
                            table,
                            column,
                            constraint
                    );
                }
            }
        } else {
            log.warn(
                    "Database integrity violation: {}",
                    rootCause != null ? rootCause.getMessage() : e.getMessage()
            );
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(
                        HttpStatus.CONFLICT.value(),
                        message
                ));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "Request data is invalid";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {
        String message = "Request body is invalid or has unsupported values";
        String detail = e.getMostSpecificCause() != null
                ? e.getMostSpecificCause().getMessage()
                : e.getMessage();
        if (detail != null && detail.contains("PaymentType")) {
            message = "Payment type is invalid. Accepted values are COD and CREDIT";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleS3Exception(
            S3Exception e) {
        String message = e.awsErrorDetails() != null && e.awsErrorDetails().errorMessage() != null
                ? e.awsErrorDetails().errorMessage()
                : "S3 upload failed";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }private String buildUniqueMessage(String column, String constraint) {
        // Map các constraint nghiệp vụ đã đặt tên về message rõ ràng, KHÔNG lộ tên
        // constraint (kể cả tên Hibernate sinh) ra frontend.
        if (com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver.UQ_TRIP_DRIVER
                .equalsIgnoreCase(constraint)) {
            return com.overcode250204.smartlogicticssystem.exception.DriverErrorCode.Messages.DRIVER_DUPLICATE_IN_TRIP;
        }

        if (column != null && !column.isBlank()) {
            return "Giá trị của trường '" + column
                    + "' đã tồn tại. Vui lòng sử dụng giá trị khác.";
        }

        // Không đưa tên constraint ra ngoài để tránh lộ chi tiết schema.
        return "Dữ liệu đã tồn tại hoặc bị trùng lặp.";
    }

    private String buildForeignKeyMessage(String column, String constraint) {
        if (column != null && !column.isBlank()) {
            return "Trường '" + column
                    + "' đang tham chiếu đến dữ liệu không tồn tại hoặc không hợp lệ.";
        }

        if (constraint != null && !constraint.isBlank()) {
            return "Dữ liệu liên quan không hợp lệ. Ràng buộc bị vi phạm: '"
                    + constraint + "'.";
        }

        return "Dữ liệu liên quan không tồn tại hoặc không hợp lệ.";
    }

    private String buildNotNullMessage(String column) {
        if (column != null && !column.isBlank()) {
            return "Trường bắt buộc '" + column + "' không được để trống.";
        }

        return "Có trường bắt buộc không được để trống.";
    }

    private String buildCheckConstraintMessage(String column, String constraint) {
        if (column != null && !column.isBlank()) {
            return "Giá trị của trường '" + column
                    + "' không thỏa điều kiện hợp lệ.";
        }

        if (constraint != null && !constraint.isBlank()) {
            return "Dữ liệu không thỏa điều kiện của ràng buộc '"
                    + constraint + "'.";
        }

        return "Dữ liệu không thỏa điều kiện hợp lệ.";
    }

    private String buildGeneralDatabaseMessage(
            String table,
            String column,
            String constraint
    ) {
        if (column != null && !column.isBlank()) {
            return "Dữ liệu tại trường '" + column
                    + "' vi phạm ràng buộc cơ sở dữ liệu.";
        }

        if (constraint != null && !constraint.isBlank()) {
            return "Dữ liệu vi phạm ràng buộc cơ sở dữ liệu: '"
                    + constraint + "'.";
        }

        if (table != null && !table.isBlank()) {
            return "Dữ liệu trong bảng '" + table
                    + "' không hợp lệ hoặc vi phạm ràng buộc.";
        }

        return "Dữ liệu không hợp lệ hoặc vi phạm ràng buộc cơ sở dữ liệu.";
    }
}
