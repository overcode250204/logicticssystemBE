package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "route_provinces")
public class RouteProvince {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private RouteConfig routeConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_hub_id")
    private Warehouse assignedHub;

    @Column(name = "province_name", nullable = false, unique = true, length = 100)
    private String provinceName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
