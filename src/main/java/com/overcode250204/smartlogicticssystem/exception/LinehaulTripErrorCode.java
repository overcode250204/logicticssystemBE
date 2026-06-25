package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LinehaulTripErrorCode implements BaseErrorCode {
    LINEHAUL_TRIP_NOT_FOUND(HttpStatus.NOT_FOUND, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_NOT_FOUND),
    LINEHAUL_TRIP_CAN_NOT_EN_ROUTE(HttpStatus.NOT_FOUND, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_CAN_NOT_EN_ROUTE);

    private final String message;
    private final HttpStatus httpStatus;

    LinehaulTripErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String LINEHAUL_TRIP_NOT_FOUND = "Linehaul trip not found";
        public static final String LINEHAUL_TRIP_CAN_NOT_EN_ROUTE = "Linehaul trip can not depart because pallets are creating or is empty";
    }
}
