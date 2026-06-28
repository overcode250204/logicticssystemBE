package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinehaulTripMapper {
    private final VehicleMapper vehicleMapper;
    private final RouteConfigMapper routeConfigMapper;
    private final LinehaulTripDriverMapper driverMapper;


    public LinehaulTripResponseDTO toResponse(LinehaulTrip entity) {
        if (entity == null) {
            return null;
        }


        return LinehaulTripResponseDTO.builder()
                .linehaulId(entity.getLinehaulId())
                .routeConfigResponseDTO(routeConfigMapper.toResponse(entity.getRouteConfig()))
                .linehaulTripDriverResponseDTO( entity.getTripDrivers() != null ?
                        entity.getTripDrivers()
                                .stream()
                                .map(driverMapper::toResponse).toList() : null)
                .vehicleResponseDTO(vehicleMapper.toResponse(entity.getVehicle()))
                .status(entity.getStatus())
                .departureTime(entity.getDepartureTime())
                .arrivalTime(entity.getArrivalTime())
                .build();
    }

//    public RouteConfig toEntity(RouteConfigCreateRequest request) {
//        if (request == null) {
//            return null;
//        }
//
//        RouteConfig entity = new RouteConfig();
//        entity.setRouteName(request.getRouteName());
//        entity.setDispatchType(request.getDispatchType());
//        entity.setFixedDispatchTime(request.getFixedDispatchTime());
//        entity.setMinCapacityPercentage(request.getMinCapacityPercentage() != null ? request.getMinCapacityPercentage() : 80);
//        entity.setCutoffTime(request.getCutoffTime());
//        entity.setMaxWaitingDays(request.getMaxWaitingDays() != null ? request.getMaxWaitingDays() : 3);
//
//        return entity;
//    }

//    public void updateEntity(LinehaulTripUpdateRequest request, LinehaulTrip entity) {
//        if (request == null || entity == null) {
//            return;
//        }
//
//        entity.setStatus(request.getStatus());
//        entity.setFixedDispatchTime(request.getFixedDispatchTime());
//
//        if (request.getMinCapacityPercentage() != null) {
//            entity.setMinCapacityPercentage(request.getMinCapacityPercentage());
//        }
//
//        entity.setCutoffTime(request.getCutoffTime());
//
//        if (request.getMaxWaitingDays() != null) {
//            entity.setMaxWaitingDays(request.getMaxWaitingDays());
//        }
//    }
}
