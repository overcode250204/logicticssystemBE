package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productid")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierid", nullable = false)
    private Supplier supplier;

    @Column(name = "productcode", nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(name = "productname", nullable = false, length = 150)
    private String productName;

    @Column(name = "minstocklevel")
    private Integer minStockLevel = 10;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}