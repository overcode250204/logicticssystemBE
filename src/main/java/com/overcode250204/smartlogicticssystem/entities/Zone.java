package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;


@Entity
@Table(name = "zones")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "name")
    private String name;

    @Column(columnDefinition = "geometry(Polygon, 4326)", name = "polygon")
    private Polygon polygon;

    @Column(name = "sla_hours")
    private Integer slaHours;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

}