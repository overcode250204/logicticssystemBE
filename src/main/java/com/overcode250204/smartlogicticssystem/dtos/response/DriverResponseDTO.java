package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import com.overcode250204.smartlogicticssystem.enums.DriverType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponseDTO {

    private Long driverId;

    private String name;

    private String phone;

    private VehicleResponseDTO currentVehicle;

    private DriverStatus status;

    private DriverType driverType;

    private WarehouseResponseDTO currentWarehouse;

    private ZoneResponseDTO zone;

}
