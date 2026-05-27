package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventorybatches")
public class InventoryBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batchid")
    private Long batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productid", nullable = false)
    private Product product;

    @Column(name = "importdate")
    private LocalDateTime importDate = LocalDateTime.now();

    @Column(name = "expirationdate")
    private LocalDateTime expirationDate;

    @Column(name = "barcode", unique = true)
    private String barcode;

    @Column(name = "barcodeimageurl")
    private String barcodeImageUrl;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "remainingquantity", nullable = false)
    private Integer remainingQuantity;

    @Column(name = "status")
    private String status = "Good";
}