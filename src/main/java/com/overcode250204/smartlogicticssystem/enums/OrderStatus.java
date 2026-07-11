package com.overcode250204.smartlogicticssystem.enums;

public enum OrderStatus {
    NEW, READY_TO_PICK,
    IN_PALLET,
    IN_TRANSIT_LINEHAUL,
    ARRIVED_AT_HUB,
    IN_TRANSIT_LOCAL,
    ARRIVED_AT_DELIVERY_POINT,
    DELIVERED, FAILED, CANCELLED
}
