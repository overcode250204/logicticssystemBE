package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.RouteConfigResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.entities.RouteProvince;
import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.exception.RouteConfigErrorCode;
import com.overcode250204.smartlogicticssystem.exception.RouteProvinceErrorCode;
import com.overcode250204.smartlogicticssystem.exception.WarehouseErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.RouteConfigMapper;
import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteProvinceRepository;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.exception.VehicleErrorCode;
import com.overcode250204.smartlogicticssystem.services.IRouteConfigService;
import com.overcode250204.smartlogicticssystem.vrp.OsrmRoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteConfigServiceImpl extends BaseServiceImpl implements IRouteConfigService {

    private final RouteConfigRepository routeConfigRepository;
    private final WarehouseRepository warehouseRepository;
    private final RouteConfigMapper routeConfigMapper;
    private final RouteProvinceRepository routeProvinceRepository;
    private final VehicleRepository vehicleRepository;
    private final OsrmRoutingService osrmRoutingService;

    private void checkAdminRole(int roleId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    @Override
    public List<RouteConfigResponseDTO> getAllRouteConfigs(int roleId, int userId) {
        checkAdminRole(roleId);
        return routeConfigRepository.findAll().stream()
                .map(routeConfigMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RouteConfigResponseDTO getById(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        RouteConfig routeConfig = findByIdOrThrow(routeConfigRepository, id, RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);
        return routeConfigMapper.toResponse(routeConfig);
    }

    @Override
    public RouteConfigResponseDTO create(RouteConfigCreateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        
        Warehouse fromWarehouse = findByIdOrThrow(warehouseRepository, request.getFromWarehouseId(), WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        Warehouse toWarehouse = findByIdOrThrow(warehouseRepository, request.getToWarehouseId(), WarehouseErrorCode.WAREHOUSE_NOT_FOUND);

        RouteConfig routeConfig = routeConfigMapper.toEntity(request);
        routeConfig.setFromWarehouse(fromWarehouse);
        routeConfig.setToWarehouse(toWarehouse);
        calculateAndSetSlaHours(routeConfig, fromWarehouse, toWarehouse);

        if (request.getDefaultVehicleId() != null) {
            Vehicle vehicle = findByIdOrThrow(vehicleRepository, request.getDefaultVehicleId(), VehicleErrorCode.VEHICLE_NOT_FOUND);
            routeConfig.setDefaultVehicle(vehicle);
        }

        if (request.getProvinceNames() != null && !request.getProvinceNames().isEmpty()) {
            for (String provinceName : request.getProvinceNames()) {
                if (routeProvinceRepository.existsByProvinceName(provinceName)) {
                    throw new AppException(RouteProvinceErrorCode.PROVINCE_NAME_ALREADY_EXISTS);
                }
                RouteProvince rp = new RouteProvince();
                rp.setProvinceName(provinceName);
                rp.setRouteConfig(routeConfig);
                rp.setAssignedHub(toWarehouse);
                routeConfig.getRouteProvinces().add(rp);
            }
        }

        RouteConfig savedRouteConfig = routeConfigRepository.save(routeConfig);
        return routeConfigMapper.toResponse(savedRouteConfig);
    }

    @Override
    public RouteConfigResponseDTO update(Long id, RouteConfigUpdateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        
        RouteConfig routeConfig = findByIdOrThrow(routeConfigRepository, id, RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);
        Warehouse fromWarehouse = findByIdOrThrow(warehouseRepository, request.getFromWarehouseId(), WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        Warehouse toWarehouse = findByIdOrThrow(warehouseRepository, request.getToWarehouseId(), WarehouseErrorCode.WAREHOUSE_NOT_FOUND);

        routeConfigMapper.updateEntity(request, routeConfig);
        routeConfig.setFromWarehouse(fromWarehouse);
        routeConfig.setToWarehouse(toWarehouse);
        calculateAndSetSlaHours(routeConfig, fromWarehouse, toWarehouse);

        if (request.getDefaultVehicleId() != null) {
            Vehicle vehicle = findByIdOrThrow(vehicleRepository, request.getDefaultVehicleId(), VehicleErrorCode.VEHICLE_NOT_FOUND);
            routeConfig.setDefaultVehicle(vehicle);
        } else {
            routeConfig.setDefaultVehicle(null);
        }

        if (request.getProvinceNames() != null) {
            // Remove provinces not in the list
            routeConfig.getRouteProvinces().removeIf(rp -> !request.getProvinceNames().contains(rp.getProvinceName()));
            
            // Add new provinces
            List<String> existingNames = routeConfig.getRouteProvinces().stream()
                    .map(RouteProvince::getProvinceName)
                    .collect(Collectors.toList());

            for (String newProvinceName : request.getProvinceNames()) {
                if (!existingNames.contains(newProvinceName)) {
                    if (routeProvinceRepository.existsByProvinceName(newProvinceName)) {
                        throw new AppException(RouteProvinceErrorCode.PROVINCE_NAME_ALREADY_EXISTS);
                    }
                    RouteProvince rp = new RouteProvince();
                    rp.setProvinceName(newProvinceName);
                    rp.setRouteConfig(routeConfig);
                    routeConfig.getRouteProvinces().add(rp);
                }
            }
            
            // Ensure all assigned hubs are synced with the new toWarehouse
            for (RouteProvince rp : routeConfig.getRouteProvinces()) {
                rp.setAssignedHub(toWarehouse);
            }
        } else {
            routeConfig.getRouteProvinces().clear();
        }

        RouteConfig updatedRouteConfig = routeConfigRepository.save(routeConfig);
        return routeConfigMapper.toResponse(updatedRouteConfig);
    }

    @Override
    public void delete(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        RouteConfig routeConfig = findByIdOrThrow(routeConfigRepository, id, RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);
        routeConfigRepository.delete(routeConfig);
    }

    private void calculateAndSetSlaHours(RouteConfig routeConfig, Warehouse from, Warehouse to) {
        if (from.getLocation() != null && to.getLocation() != null) {
            double lat1 = from.getLocation().getY();
            double lon1 = from.getLocation().getX();
            double lat2 = to.getLocation().getY();
            double lon2 = to.getLocation().getX();
            double durationSeconds = osrmRoutingService.getTravelDurationSeconds(lat1, lon1, lat2, lon2);
            int hours = (int) Math.ceil(durationSeconds / 3600.0);
            routeConfig.setSlaHours(hours);
        } else {
            routeConfig.setSlaHours(0);
        }
    }
}
