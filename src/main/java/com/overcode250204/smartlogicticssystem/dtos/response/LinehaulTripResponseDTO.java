package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LinehaulTripResponseDTO {

    private Long linehaulId;


    private RouteConfigResponseDTO routeConfig;


    private List<LinehaulTripDriverResponseDTO> linehaulTripDriver;

    private List<PalletResponseDTO> pallets;


    private VehicleResponseDTO vehicle;


    private LinehaulTripStatus status;


    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

}
