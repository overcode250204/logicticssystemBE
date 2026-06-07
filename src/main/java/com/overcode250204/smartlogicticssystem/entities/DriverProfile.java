package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "driverprofile")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipperid")
    private Long id;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "platenumber", nullable = false)
    private String plateNumber;

    @Column(name = "maxweightcapacity")
    private Integer maxWeightCapacity = 30000;

    @Column(length = 30)
    private String status = "ACTIVE";// ACTIVE, INACTIVE, SUSPENDED
}


