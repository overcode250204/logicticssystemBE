package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PalletErrorCode implements BaseErrorCode {
    PALLET_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.PALLET_NOT_FOUND),
    PALLET_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, Messages.PALLET_CANNOT_UPDATE),
    PALLET_CANNOT_DELETE(HttpStatus.BAD_REQUEST, Messages.PALLET_CANNOT_DELETE),
    ROUTE_MISMATCH(HttpStatus.BAD_REQUEST, Messages.ROUTE_MISMATCH),
    CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, Messages.CAPACITY_EXCEEDED),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, Messages.INVALID_ORDER_STATUS),
    LINEHAUL_TRIP_MISSING_CONFIG(HttpStatus.BAD_REQUEST, Messages.LINEHAUL_TRIP_MISSING_CONFIG);

    private final String message;
    private final HttpStatus httpStatus;

    PalletErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String PALLET_NOT_FOUND = "Pallet not found";
        public static final String PALLET_CANNOT_UPDATE = "Pallet can only be updated when status is CREATING";
        public static final String PALLET_CANNOT_DELETE = "Pallet can only be deleted when status is CREATING or SEALED";
        public static final String ROUTE_MISMATCH = "Order must belong to the route of the linehaul trip";
        public static final String CAPACITY_EXCEEDED = "Pallet weight or volume exceeds vehicle capacity constraints";
        public static final String INVALID_ORDER_STATUS = "Only orders with status NEW can be added to a pallet";
        public static final String LINEHAUL_TRIP_MISSING_CONFIG = "Linehaul trip must have route config and vehicle assigned";
    }
}
