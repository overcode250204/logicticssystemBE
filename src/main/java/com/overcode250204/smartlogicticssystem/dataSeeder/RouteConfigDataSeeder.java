//package com.overcode250204.smartlogicticssystem.dataSeeder;
//
//import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
//import com.overcode250204.smartlogicticssystem.entities.RouteProvince;
//import com.overcode250204.smartlogicticssystem.entities.Warehouse;
//import com.overcode250204.smartlogicticssystem.enums.DispatchType;
//import com.overcode250204.smartlogicticssystem.enums.WarehouseType;
//import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
//import com.overcode250204.smartlogicticssystem.repositories.RouteProvinceRepository;
//import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Point;
//import org.springframework.stereotype.Component;
//
//import java.text.Normalizer;
//import java.time.LocalTime;
//import java.util.Locale;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class RouteConfigDataSeeder implements DataSeeder {
//
//    private final WarehouseRepository warehouseRepository;
//    private final RouteConfigRepository routeConfigRepository;
//    private final RouteProvinceRepository routeProvinceRepository;
//    private final GeometryFactory geometryFactory = new GeometryFactory();
//
//    @Override
//    public int getOrder() {
//        return 8;
//    }
//
//    @Override
//    public void seed() {
//        boolean hcmProvinceExists = routeProvinceRepository.findAll().stream()
//                .anyMatch(routeProvince -> normalizeProvinceName(routeProvince.getProvinceName()).equals("hochiminh"));
//        if (hcmProvinceExists) {
//            return;
//        }
//
//        Warehouse cdc = warehouseRepository.findAll().stream()
//                .filter(warehouse -> warehouse.getType() == WarehouseType.CDC)
//                .findFirst()
//                .orElseGet(() -> createWarehouse(
//                        "CDC Hồ Chí Minh",
//                        WarehouseType.CDC,
//                        "Khu logistics Cát Lái, Thành phố Hồ Chí Minh",
//                        "Thành phố Hồ Chí Minh",
//                        106.7850,
//                        10.7631
//                ));
//
//        Warehouse hub = warehouseRepository.findAll().stream()
//                .filter(warehouse -> warehouse.getType() == WarehouseType.HUB)
//                .findFirst()
//                .orElseGet(() -> createWarehouse(
//                        "Hub Hồ Chí Minh",
//                        WarehouseType.HUB,
//                        "Quận 7, Thành phố Hồ Chí Minh",
//                        "Thành phố Hồ Chí Minh",
//                        106.7218,
//                        10.7297
//                ));
//
//        RouteConfig routeConfig = new RouteConfig();
//        routeConfig.setRouteName("HCM Local Route");
//        routeConfig.setFromWarehouse(cdc);
//        routeConfig.setToWarehouse(hub);
//        routeConfig.setDispatchType(DispatchType.HYBRID);
//        routeConfig.setFixedDispatchTime(LocalTime.of(17, 0));
//        routeConfig.setCutoffTime(LocalTime.of(16, 30));
//        routeConfig.setMinCapacityPercentage(80);
//        routeConfig.setMaxWaitingDays(1);
//        routeConfig.setIsActive(true);
//        routeConfig.setSlaHours(24);
//
//        RouteProvince routeProvince = new RouteProvince();
//        routeProvince.setProvinceName("Thành phố Hồ Chí Minh");
//        routeProvince.setAssignedHub(hub);
//        routeProvince.setRouteConfig(routeConfig);
//        routeConfig.getRouteProvinces().add(routeProvince);
//
//        routeConfigRepository.save(routeConfig);
//        log.info("Seeded default HCM route config and province");
//    }
//
//    private Warehouse createWarehouse(
//            String name,
//            WarehouseType type,
//            String address,
//            String province,
//            double longitude,
//            double latitude
//    ) {
//        Warehouse warehouse = new Warehouse();
//        warehouse.setName(name);
//        warehouse.setType(type);
//        warehouse.setAddress(address);
//        warehouse.setProvince(province);
//        warehouse.setStartDeliveryTime(LocalTime.of(8, 0));
//        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
//        point.setSRID(4326);
//        warehouse.setLocation(point);
//        return warehouseRepository.save(warehouse);
//    }
//
//    private String normalizeProvinceName(String provinceName) {
//        if (provinceName == null) {
//            return "";
//        }
//        String normalized = Normalizer.normalize(provinceName, Normalizer.Form.NFD)
//                .replaceAll("\\p{M}", "")
//                .replace('\u0111', 'd')
//                .replace('\u0110', 'D')
//                .toLowerCase(Locale.ROOT);
//        normalized = normalized.replaceAll("\\b(thanh pho|tinh|tp|city|province)\\b", "");
//        normalized = normalized.replaceAll("[^a-z0-9]", "");
//        if (normalized.equals("hcm") || normalized.equals("tphcm") || normalized.equals("saigon")) {
//            return "hochiminh";
//        }
//        return normalized;
//    }
//}
