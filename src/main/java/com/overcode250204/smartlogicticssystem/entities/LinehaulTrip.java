package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "linehaul_trips")
public class LinehaulTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "linehaul_id")
    private Long linehaulId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private RouteConfig routeConfig;

    @OneToMany(
            mappedBy = "linehaulTrip",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LinehaulTripDriver> tripDrivers = new ArrayList<>();

    @OneToMany(
            mappedBy = "linehaulTrip"
    )
    private List<Pallet> pallets = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private LinehaulTripStatus status = LinehaulTripStatus.PREPARING;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "is_created_system")
    private Boolean isCreatedSystem;

    @Column(name = "linehaul_trip_code", unique = true, length = 20)
    private String linehaulTripCode;
}
