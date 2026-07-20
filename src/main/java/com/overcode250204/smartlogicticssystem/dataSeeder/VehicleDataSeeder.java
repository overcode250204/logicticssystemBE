package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.enums.VehicleType;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleDataSeeder implements DataSeeder {

    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public int getOrder() {
        return 9;
    }

    @Override
    @Transactional
    public void seed() {
        Warehouse defaultWarehouse = warehouseRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        List<VehicleSeed> vehicles = List.of(
                new VehicleSeed("51A-10001", VehicleType.BIG_TRUCK, "15000", "60", "9.600", "2.400", "2.600", VehicleStatus.ACTIVE),
                new VehicleSeed("51A-10002", VehicleType.BIG_TRUCK, "12000", "50", "8.500", "2.350", "2.500", VehicleStatus.ACTIVE),
                new VehicleSeed("51C-20001", VehicleType.SMALL_TRUCK, "3500", "18", "4.300", "1.900", "2.200", VehicleStatus.ACTIVE),
                new VehicleSeed("51C-20002", VehicleType.SMALL_TRUCK, "2500", "14", "3.800", "1.800", "2.000", VehicleStatus.ACTIVE),
                new VehicleSeed("59B1-30001", VehicleType.BIKE, "80", "0.25", "0.600", "0.450", "0.900", VehicleStatus.ACTIVE),
                new VehicleSeed("59B1-30002", VehicleType.BIKE, "80", "0.25", "0.600", "0.450", "0.900", VehicleStatus.ACTIVE),
                new VehicleSeed("51A-90001", VehicleType.BIG_TRUCK, "15000", "60", "9.600", "2.400", "2.600", VehicleStatus.MAINTENANCE)
        );

        vehicles.forEach(seed -> createVehicleIfNotExists(seed, defaultWarehouse));
    }

    private void createVehicleIfNotExists(VehicleSeed seed, Warehouse warehouse) {
        if (vehicleRepository.existsByLicensePlate(seed.licensePlate())) {
            return;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(seed.licensePlate());
        vehicle.setVehicleType(seed.vehicleType());
        vehicle.setMaxWeightKg(new BigDecimal(seed.maxWeightKg()));
        vehicle.setMaxVolumeM3(new BigDecimal(seed.maxVolumeM3()));
        vehicle.setCargoLength(new BigDecimal(seed.cargoLength()));
        vehicle.setCargoWidth(new BigDecimal(seed.cargoWidth()));
        vehicle.setCargoHeight(new BigDecimal(seed.cargoHeight()));
        vehicle.setStatus(seed.status());
        vehicle.setCurrentWarehouse(warehouse);

        vehicleRepository.save(vehicle);
        log.info("Seeded vehicle: {}", seed.licensePlate());
    }

    private record VehicleSeed(
            String licensePlate,
            VehicleType vehicleType,
            String maxWeightKg,
            String maxVolumeM3,
            String cargoLength,
            String cargoWidth,
            String cargoHeight,
            VehicleStatus status
    ) {
    }
}
