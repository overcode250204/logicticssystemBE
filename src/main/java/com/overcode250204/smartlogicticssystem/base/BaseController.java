package com.overcode250204.smartlogicticssystem.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    protected <T> ResponseEntity<BaseResponse<T>> success(T data, String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity<BaseResponse<T>> error(HttpStatus status, String message) {
        BaseResponse<T> response = BaseResponse.<T>builder()
                .status(status.value())
                .message(message)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
