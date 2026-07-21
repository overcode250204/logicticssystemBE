package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.entities.RouteProvince;
import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.enums.DispatchType;
import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteProvinceRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteConfigDataSeeder implements DataSeeder {

    public static final String HCM_ROUTE = "HCM Main Distribution Route";
    public static final String DONG_NAI_ROUTE = "HCM to Dong Nai Route";

    private final WarehouseRepository warehouseRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteConfigRepository routeConfigRepository;
    private final RouteProvinceRepository routeProvinceRepository;

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    @Transactional
    public void seed() {
        Warehouse cdc = requireWarehouse(WarehouseZoneDataSeeder.HCM_CDC);
        Warehouse thuDucHub = requireWarehouse(WarehouseZoneDataSeeder.THU_DUC_HUB);
        Warehouse bienHoaHub = requireWarehouse(WarehouseZoneDataSeeder.BIEN_HOA_HUB);

        RouteConfig hcmRoute = createRoute(
                HCM_ROUTE, cdc, thuDucHub, requireVehicle("51A-10001"),
                DispatchType.HYBRID, LocalTime.of(17, 0), LocalTime.of(16, 30), 80, 1, 24
        );
        createProvince("Ho Chi Minh", thuDucHub, hcmRoute);
        createProvince("Binh Duong", thuDucHub, hcmRoute);

        RouteConfig dongNaiRoute = createRoute(
                DONG_NAI_ROUTE, cdc, bienHoaHub, requireVehicle("51A-10002"),
                DispatchType.TIME, LocalTime.of(18, 0), LocalTime.of(17, 0), 70, 2, 36
        );
        createProvince("Dong Nai", bienHoaHub, dongNaiRoute);

        log.info("Route configuration seed data completed.");
    }

    private RouteConfig createRoute(
            String name,
            Warehouse from,
            Warehouse to,
            Vehicle defaultVehicle,
            DispatchType dispatchType,
            LocalTime dispatchTime,
            LocalTime cutoffTime,
            int minimumCapacity,
            int maximumWaitingDays,
            int slaHours
    ) {
        return routeConfigRepository.findByRouteNameIgnoreCase(name)
                .orElseGet(() -> {
                    RouteConfig route = new RouteConfig();
                    route.setRouteName(name);
                    route.setFromWarehouse(from);
                    route.setToWarehouse(to);
                    route.setDefaultVehicle(defaultVehicle);
                    route.setDispatchType(dispatchType);
                    route.setFixedDispatchTime(dispatchTime);
                    route.setCutoffTime(cutoffTime);
                    route.setMinCapacityPercentage(minimumCapacity);
                    route.setMaxWaitingDays(maximumWaitingDays);
                    route.setSlaHours(slaHours);
                    route.setIsActive(true);
                    log.info("Created route: {}", name);
                    return routeConfigRepository.save(route);
                });
    }

    private void createProvince(String provinceName, Warehouse assignedHub, RouteConfig route) {
        if (routeProvinceRepository.existsByProvinceName(provinceName)) {
            return;
        }

        RouteProvince province = new RouteProvince();
        province.setProvinceName(provinceName);
        province.setAssignedHub(assignedHub);
        province.setRouteConfig(route);
        routeProvinceRepository.save(province);
    }

    private Warehouse requireWarehouse(String name) {
        return warehouseRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalStateException("Required warehouse not found: " + name));
    }

    private Vehicle requireVehicle(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalStateException("Required vehicle not found: " + licensePlate));
    }
}
