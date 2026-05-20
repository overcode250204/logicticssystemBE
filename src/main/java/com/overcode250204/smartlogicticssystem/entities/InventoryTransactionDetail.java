package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventorytransactiondetails")
public class InventoryTransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detailid")
    private Long detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionid", nullable = false)
    private InventoryTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batchid", nullable = false)
    private InventoryBatch batch;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
