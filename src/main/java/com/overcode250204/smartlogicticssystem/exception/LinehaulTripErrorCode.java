package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LinehaulTripErrorCode implements BaseErrorCode {
    LINEHAUL_TRIP_NOT_FOUND(HttpStatus.NOT_FOUND, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_NOT_FOUND),
    LINEHAUL_TRIP_CAN_NOT_EN_ROUTE(HttpStatus.NOT_FOUND, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_CAN_NOT_EN_ROUTE),
    LINEHAUL_TRIP_CAN_NOT_UPDATE(HttpStatus.NOT_FOUND, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_CAN_NOT_UPDATE),
    LINEHAUL_TRIP_CAN_NOT_DELETE(HttpStatus.NOT_FOUND, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_CAN_NOT_DELETE),
    LINEHAUL_TRIP_CAN_NOT_UPDATE_ROUTE(HttpStatus.NOT_FOUND, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_CAN_NOT_UPDATE_ROUTE),
    LINEHAUL_TRIP_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, LinehaulTripErrorCode.Messages.LINEHAUL_TRIP_CAPACITY_EXCEEDED),
    GPS_NOT_NEAR_FROM_WAREHOUSE(HttpStatus.BAD_REQUEST, LinehaulTripErrorCode.Messages.GPS_NOT_NEAR_FROM_WAREHOUSE),
    GPS_NOT_NEAR_TO_WAREHOUSE(HttpStatus.BAD_REQUEST, LinehaulTripErrorCode.Messages.GPS_NOT_NEAR_TO_WAREHOUSE);

    private final String message;
    private final HttpStatus httpStatus;

    LinehaulTripErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String LINEHAUL_TRIP_NOT_FOUND = "Linehaul trip not found";
        public static final String LINEHAUL_TRIP_CAN_NOT_EN_ROUTE = "Linehaul trip can not depart because pallets are sealing or is empty";
        public static final String LINEHAUL_TRIP_CAN_NOT_UPDATE = "Linehaul trip can not update because linehaul trip are en route";
        public static final String LINEHAUL_TRIP_CAN_NOT_DELETE = "Linehaul trip can not delete because linehaul trip are en route";
        public static final String LINEHAUL_TRIP_CAN_NOT_UPDATE_ROUTE = "Linehaul trip can not update route config because pallets are sealing or created";
        public static final String LINEHAUL_TRIP_CAPACITY_EXCEEDED = "Total pallet weight or volume exceeds vehicle capacity constraints";
        public static final String GPS_NOT_NEAR_FROM_WAREHOUSE = "GPS location must be within 500m of the origin warehouse";
        public static final String GPS_NOT_NEAR_TO_WAREHOUSE = "GPS location must be within 500m of the destination warehouse";

    }
}
