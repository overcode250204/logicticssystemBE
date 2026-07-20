package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pallets")
public class Pallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pallet_id")
    private Long palletId;

    @Column(name = "pallet_code", unique = true, length = 20)
    private String palletCode;

    @Column(name = "barcode_url")
    private String barcodeUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private RouteConfig routeConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linehaul_id")
    private LinehaulTrip linehaulTrip;

    @Column(name = "total_weight_kg", nullable = false)
    private BigDecimal totalWeightKg = new BigDecimal(0);

    @Column(name = "total_volume_m3", nullable = false)
    private BigDecimal totalVolumeM3 = new BigDecimal(0);

    @OneToMany(
            mappedBy = "pallet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PalletItem> palletItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PalletStatus status = PalletStatus.CREATING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_created_system")
    private Boolean isCreatedSystem = false;
}
