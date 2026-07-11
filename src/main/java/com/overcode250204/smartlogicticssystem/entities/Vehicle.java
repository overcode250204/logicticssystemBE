package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "license_plate", unique = true, nullable = false, length = 20)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", length = 20)
    private VehicleType vehicleType;

    @Column(name = "max_weight_kg", nullable = false)
    private BigDecimal maxWeightKg;

    @Column(name = "length", precision = 10, scale = 3)
    private BigDecimal cargoLength;

    @Column(name = "width", precision = 10, scale = 3)
    private BigDecimal cargoWidth;

    @Column(name = "height", precision = 10, scale = 3)
    private BigDecimal cargoHeight;

    @Column(name = "max_volume_m3", nullable = false)
    private BigDecimal maxVolumeM3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_warehouse_id")
    private Warehouse currentWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private VehicleStatus status = VehicleStatus.ACTIVE;
}
