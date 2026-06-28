package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import com.overcode250204.smartlogicticssystem.enums.DriverType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", unique = true, nullable = false, length = 15)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_vehicle_id")
    private Vehicle currentVehicle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private DriverStatus status = DriverStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_type", length = 20)
    private DriverType driverType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_warehouse_id")
    private Warehouse currentWarehouse;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

}
