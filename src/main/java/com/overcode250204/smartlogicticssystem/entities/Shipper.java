package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "shippers")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipperid")
    private Long id;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "maxweightcapacity")
    private Integer maxWeightCapacity = 30000;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homezoneid")
    private Zone homeZone;

    @Column(length = 30)
    private String status = "ACTIVE";// ACTIVE, INACTIVE, SUSPENDED
}


