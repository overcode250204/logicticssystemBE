package com.overcode250204.smartlogicticssystem.entities;


import com.overcode250204.smartlogicticssystem.enums.DriverRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "linehaul_trip_driver",
        // Một tài xế chỉ được gán một lần cho mỗi chuyến (bất kể role).
        // KHÔNG dùng (linehaul_id, driver_id, role) vì vẫn cho phép trùng driver
        // ở hai role khác nhau. Constraint được đặt tên cố định để error handler
        // map về DRIVER_DUPLICATE_IN_TRIP mà không lộ tên Hibernate sinh.
        uniqueConstraints = @UniqueConstraint(
                name = LinehaulTripDriver.UQ_TRIP_DRIVER,
                columnNames = {"linehaul_id", "driver_id"}
        )
)
public class LinehaulTripDriver {

    public static final String UQ_TRIP_DRIVER = "uq_ltd_trip_driver";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linehaul_id")
    private LinehaulTrip linehaulTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DriverRole role;

    private LocalDateTime assignedAt;
}
