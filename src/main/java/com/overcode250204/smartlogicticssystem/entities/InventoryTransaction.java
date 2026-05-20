package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.InventoryStrategy;
import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventorytransactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionid")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productid", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InventoryTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy")
    private InventoryStrategy strategy;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "createdat")
    private LocalDateTime createdAt = LocalDateTime.now();
}
