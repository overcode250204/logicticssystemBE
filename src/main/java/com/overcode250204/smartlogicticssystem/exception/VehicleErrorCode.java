package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum VehicleErrorCode implements BaseErrorCode {
    VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.VEHICLE_NOT_FOUND),
    LICENSE_PLATE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Messages.LICENSE_PLATE_ALREADY_EXISTS);

    private final String message;
    private final HttpStatus httpStatus;

    VehicleErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String VEHICLE_NOT_FOUND = "Vehicle not found";
        public static final String LICENSE_PLATE_ALREADY_EXISTS = "License plate already exists";
    }
}
