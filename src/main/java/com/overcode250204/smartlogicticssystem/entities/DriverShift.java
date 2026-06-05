package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "drivershifts")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipperid", nullable = false)
    private Shipper shipper;

    @Column(name = "starttime", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "endtime", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "workstatus", length = 30)
    private String workStatus = "OFF"; // ON_SHIFT (Trong ca), OFF_SHIFT (Hết ca), BREAK (Nghỉ giữa ca)
}

