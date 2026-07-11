package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.DispatchType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "route_configs")
public class RouteConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "route_name", nullable = false, length = 100)
    private String routeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id")
    private Warehouse toWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispatch_type", length = 20)
    private DispatchType dispatchType;

    @Column(name = "fixed_dispatch_time")
    private LocalTime fixedDispatchTime;

    @Column(name = "min_capacity_percentage")
    private Integer minCapacityPercentage = 80;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_vehicle_id")
    private Vehicle defaultVehicle;

    @Column(name = "cutoff_time", nullable = false)
    private LocalTime cutoffTime;

    @Column(name = "max_waiting_days")
    private Integer maxWaitingDays = 3;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "routeConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteProvince> routeProvinces = new ArrayList<>();
}
