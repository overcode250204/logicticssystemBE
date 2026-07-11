package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.RouteConfigResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.entities.RouteProvince;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RouteConfigMapper {

    private final WarehouseMapper warehouseMapper;
    private final VehicleMapper vehicleMapper;

    public RouteConfigResponseDTO toResponse(RouteConfig entity) {
        if (entity == null) {
            return null;
        }

        List<String> provinceNames = null;
        if (entity.getRouteProvinces() != null) {
            provinceNames = entity.getRouteProvinces().stream()
                    .map(RouteProvince::getProvinceName)
                    .collect(Collectors.toList());
        }

        return RouteConfigResponseDTO.builder()
                .routeId(entity.getRouteId())
                .routeName(entity.getRouteName())
                .fromWarehouse(warehouseMapper.toResponse(entity.getFromWarehouse()))
                .toWarehouse(warehouseMapper.toResponse(entity.getToWarehouse()))
                .dispatchType(entity.getDispatchType())
                .defaultVehicle(vehicleMapper.toResponse(entity.getDefaultVehicle()))
                .fixedDispatchTime(entity.getFixedDispatchTime())
                .minCapacityPercentage(entity.getMinCapacityPercentage())
                .cutoffTime(entity.getCutoffTime())
                .maxWaitingDays(entity.getMaxWaitingDays())
                .provinceNames(provinceNames)
                .isActive(entity.getIsActive())
                .build();
    }

    public RouteConfig toEntity(RouteConfigCreateRequest request) {
        if (request == null) {
            return null;
        }

        RouteConfig entity = new RouteConfig();
        entity.setRouteName(request.getRouteName());
        entity.setDispatchType(request.getDispatchType());
        entity.setFixedDispatchTime(request.getFixedDispatchTime());
        entity.setMinCapacityPercentage(request.getMinCapacityPercentage() != null ? request.getMinCapacityPercentage() : 80);
        entity.setCutoffTime(request.getCutoffTime());
        entity.setMaxWaitingDays(request.getMaxWaitingDays() != null ? request.getMaxWaitingDays() : 3);
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        return entity;
    }

    public void updateEntity(RouteConfigUpdateRequest request, RouteConfig entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setRouteName(request.getRouteName());
        entity.setDispatchType(request.getDispatchType());
        entity.setFixedDispatchTime(request.getFixedDispatchTime());
        
        if (request.getMinCapacityPercentage() != null) {
            entity.setMinCapacityPercentage(request.getMinCapacityPercentage());
        }
        
        entity.setCutoffTime(request.getCutoffTime());
        
        if (request.getMaxWaitingDays() != null) {
            entity.setMaxWaitingDays(request.getMaxWaitingDays());
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
    }
}
