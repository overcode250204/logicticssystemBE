package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import java.time.LocalDateTime;
import java.util.Set;

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
    @JoinColumn(name = "linehaul_id")
    private LinehaulTrip linehaulTrip;

    @ManyToMany
    @JoinTable(
            name = "pallet_items",
            joinColumns = @JoinColumn(name = "pallet_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private Set<Order> orders;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PalletStatus status = PalletStatus.CREATING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
