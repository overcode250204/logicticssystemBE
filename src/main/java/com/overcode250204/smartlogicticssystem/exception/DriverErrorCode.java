package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DriverErrorCode implements BaseErrorCode {
    DRIVER_NOT_FOUND(HttpStatus.NOT_FOUND, DriverErrorCode.Messages.DRIVER_NOT_FOUND),
    DRIVER_NOT_LINEHAUL(HttpStatus.BAD_REQUEST, DriverErrorCode.Messages.DRIVER_NOT_LINEHAUL),
    DRIVER_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, DriverErrorCode.Messages.DRIVER_NOT_AVAILABLE),
    DRIVER_NOT_IN_WAREHOUSE(HttpStatus.BAD_REQUEST, DriverErrorCode.Messages.DRIVER_NOT_IN_WAREHOUSE),
    MAIN_DRIVER_NOT_FOUND(HttpStatus.BAD_REQUEST, DriverErrorCode.Messages.MAIN_DRIVER_NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    DriverErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String DRIVER_NOT_FOUND = "Driver not found";
        public static final String DRIVER_NOT_LINEHAUL = "Driver is not a linehaul driver";
        public static final String DRIVER_NOT_AVAILABLE = "Driver is not available";
        public static final String DRIVER_NOT_IN_WAREHOUSE = "Driver is not in warehouse";
        public static final String MAIN_DRIVER_NOT_FOUND = "Linehaul trip must have a main driver assigned";
    }
}

