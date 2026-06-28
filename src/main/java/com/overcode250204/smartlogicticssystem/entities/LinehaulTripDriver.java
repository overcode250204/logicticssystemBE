package com.overcode250204.smartlogicticssystem.entities;


import com.overcode250204.smartlogicticssystem.enums.AssignmentStatus;
import com.overcode250204.smartlogicticssystem.enums.DriverRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "linehaul_trip_driver")
public class LinehaulTripDriver {
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AssignmentStatus assignmentStatus;

    private LocalDateTime assignedAt;
}
