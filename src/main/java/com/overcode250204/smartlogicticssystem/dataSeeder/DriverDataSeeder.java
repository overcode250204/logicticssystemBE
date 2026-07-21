package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.entities.Zone;
import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import com.overcode250204.smartlogicticssystem.enums.DriverType;
import com.overcode250204.smartlogicticssystem.repositories.DriverRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import com.overcode250204.smartlogicticssystem.repositories.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverDataSeeder implements DataSeeder {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;

    @Override
    public int getOrder() {
        return 11;
    }

    @Override
    @Transactional
    public void seed() {
        List<DriverSeed> drivers = List.of(
                new DriverSeed("taixe1@logistics.com", "0903333333", DriverType.LINEHAUL,
                        DriverStatus.AVAILABLE, "51A-10001", WarehouseZoneDataSeeder.HCM_CDC,
                        WarehouseZoneDataSeeder.EAST_HCM_ZONE),
                new DriverSeed("taixe2@logistics.com", "0903333334", DriverType.LINEHAUL,
                        DriverStatus.ON_LINEHAUL_TRIP, "51A-10002", WarehouseZoneDataSeeder.HCM_CDC,
                        WarehouseZoneDataSeeder.EAST_HCM_ZONE),
                new DriverSeed("taixe3@logistics.com", "0903333335", DriverType.LAST_MILE,
                        DriverStatus.AVAILABLE, "59B1-30001", WarehouseZoneDataSeeder.THU_DUC_HUB,
                        WarehouseZoneDataSeeder.EAST_HCM_ZONE),
                new DriverSeed("taixe4@logistics.com", "0903333336", DriverType.LAST_MILE,
                        DriverStatus.ON_LOCAL_TRIP, "59B1-30002", WarehouseZoneDataSeeder.DISTRICT_7_HUB,
                        WarehouseZoneDataSeeder.SOUTH_HCM_ZONE),
                new DriverSeed("taixe5@logistics.com", "0903333337", DriverType.LAST_MILE,
                        DriverStatus.BUSY, "51C-20001", WarehouseZoneDataSeeder.THU_DUC_HUB,
                        WarehouseZoneDataSeeder.CENTRAL_HCM_ZONE),
                new DriverSeed("taixe6@logistics.com", "0903333338", DriverType.LAST_MILE,
                        DriverStatus.OFFLINE, "51C-20002", WarehouseZoneDataSeeder.BIEN_HOA_HUB,
                        WarehouseZoneDataSeeder.BIEN_HOA_ZONE)
        );

        drivers.forEach(this::createDriverIfNotExists);
        log.info("Driver seed data completed.");
    }

    private void createDriverIfNotExists(DriverSeed seed) {
        if (driverRepository.findByPhone(seed.phone()).isPresent()) {
            return;
        }

        User user = userRepository.findByEmail(seed.userEmail())
                .orElseThrow(() -> new IllegalStateException("Driver user not found: " + seed.userEmail()));
        Vehicle vehicle = vehicleRepository.findByLicensePlate(seed.licensePlate())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found: " + seed.licensePlate()));
        Warehouse warehouse = warehouseRepository.findByNameIgnoreCase(seed.warehouseName())
                .orElseThrow(() -> new IllegalStateException("Warehouse not found: " + seed.warehouseName()));
        Zone zone = zoneRepository.findByNameIgnoreCase(seed.zoneName())
                .orElseThrow(() -> new IllegalStateException("Zone not found: " + seed.zoneName()));

        Driver driver = new Driver();
        driver.setName(user.getFullName());
        driver.setPhone(seed.phone());
        driver.setUser(user);
        driver.setDriverType(seed.driverType());
        driver.setStatus(seed.status());
        driver.setCurrentVehicle(vehicle);
        driver.setCurrentWarehouse(warehouse);
        driver.setZone(zone);
        driverRepository.save(driver);
    }

    private record DriverSeed(
            String userEmail,
            String phone,
            DriverType driverType,
            DriverStatus status,
            String licensePlate,
            String warehouseName,
            String zoneName
    ) {
    }
}
