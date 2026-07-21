package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.entities.Zone;
import com.overcode250204.smartlogicticssystem.enums.WarehouseType;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import com.overcode250204.smartlogicticssystem.repositories.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WarehouseZoneDataSeeder implements DataSeeder {

    public static final String HCM_CDC = "HCM Central Distribution Center";
    public static final String THU_DUC_HUB = "Thu Duc Delivery Hub";
    public static final String DISTRICT_7_HUB = "District 7 Delivery Hub";
    public static final String BIEN_HOA_HUB = "Bien Hoa Delivery Hub";

    public static final String CENTRAL_HCM_ZONE = "Central HCM Zone";
    public static final String EAST_HCM_ZONE = "East HCM Zone";
    public static final String SOUTH_HCM_ZONE = "South HCM Zone";
    public static final String BIEN_HOA_ZONE = "Bien Hoa Zone";

    private static final int SRID_WGS_84 = 4326;

    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public int getOrder() {
        return 7;
    }

    @Override
    @Transactional
    public void seed() {
        createWarehouse(HCM_CDC, WarehouseType.CDC,
                "Cat Lai Logistics Area, Thu Duc, Ho Chi Minh", "Ho Chi Minh",
                106.7850, 10.7631, LocalTime.of(7, 30));
        createWarehouse(THU_DUC_HUB, WarehouseType.HUB,
                "Vo Nguyen Giap, Thu Duc, Ho Chi Minh", "Ho Chi Minh",
                106.7530, 10.8500, LocalTime.of(8, 0));
        createWarehouse(DISTRICT_7_HUB, WarehouseType.HUB,
                "Nguyen Van Linh, District 7, Ho Chi Minh", "Ho Chi Minh",
                106.7218, 10.7297, LocalTime.of(8, 0));
        createWarehouse(BIEN_HOA_HUB, WarehouseType.HUB,
                "Bien Hoa Industrial Area, Dong Nai", "Dong Nai",
                106.8500, 10.9500, LocalTime.of(8, 0));

        createZone(CENTRAL_HCM_ZONE, 24,
                106.6500, 10.7400, 106.7300, 10.8200);
        createZone(EAST_HCM_ZONE, 24,
                106.7300, 10.7800, 106.8300, 10.9000);
        createZone(SOUTH_HCM_ZONE, 18,
                106.6800, 10.6800, 106.7900, 10.7600);
        createZone(BIEN_HOA_ZONE, 36,
                106.7800, 10.8800, 106.9500, 11.0500);

        log.info("Warehouse and delivery-zone seed data completed.");
    }

    private Warehouse createWarehouse(
            String name,
            WarehouseType type,
            String address,
            String province,
            double longitude,
            double latitude,
            LocalTime startDeliveryTime
    ) {
        return warehouseRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Warehouse warehouse = new Warehouse();
                    warehouse.setName(name);
                    warehouse.setType(type);
                    warehouse.setAddress(address);
                    warehouse.setProvince(province);
                    warehouse.setLocation(point(longitude, latitude));
                    warehouse.setStartDeliveryTime(startDeliveryTime);
                    log.info("Created warehouse: {}", name);
                    return warehouseRepository.save(warehouse);
                });
    }

    private Zone createZone(
            String name,
            int slaHours,
            double minLongitude,
            double minLatitude,
            double maxLongitude,
            double maxLatitude
    ) {
        return zoneRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Coordinate[] coordinates = {
                            new Coordinate(minLongitude, minLatitude),
                            new Coordinate(maxLongitude, minLatitude),
                            new Coordinate(maxLongitude, maxLatitude),
                            new Coordinate(minLongitude, maxLatitude),
                            new Coordinate(minLongitude, minLatitude)
                    };
                    Polygon polygon = geometryFactory.createPolygon(coordinates);
                    polygon.setSRID(SRID_WGS_84);

                    Zone zone = new Zone();
                    zone.setName(name);
                    zone.setSlaHours(slaHours);
                    zone.setPolygon(polygon);
                    log.info("Created delivery zone: {}", name);
                    return zoneRepository.save(zone);
                });
    }

    private Point point(double longitude, double latitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(SRID_WGS_84);
        return point;
    }
}
