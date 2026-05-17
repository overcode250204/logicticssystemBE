package com.overcode250204.smartlogicticssystem.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    protected <T> ResponseEntity<BaseResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(BaseResponse.success(data, message));
    }

    protected <T> ResponseEntity<BaseResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(BaseResponse.error(status.value(), message));
    }
}
