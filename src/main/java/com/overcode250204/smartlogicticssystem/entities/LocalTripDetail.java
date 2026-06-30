package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.LocalTripDetailStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "local_trip_details")
public class LocalTripDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_trip_id")
    private LocalTrip localTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "proof_url", length = 500)
    private String proofUrl;
    
    @Column(name = "barcode_scanned")
    private Boolean barcodeScanned = false;

     @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private LocalTripDetailStatus status = LocalTripDetailStatus.PENDING;
}
