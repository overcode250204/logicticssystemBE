package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LinehaulTripResponseDTO {

    private Long linehaulId;


    private RouteConfigResponseDTO routeConfigResponseDTO;


    private DriverResponseDTO driverResponseDTO;


    private VehicleResponseDTO vehicleResponseDTO;


    private LinehaulTripStatus status;


    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;
}
