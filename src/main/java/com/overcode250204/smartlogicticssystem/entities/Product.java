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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryid", nullable = false)
    private ProductCategory category;

    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    @Column(name = "length", precision = 10, scale = 3)
    private BigDecimal length;

    @Column(name = "width", precision = 10, scale = 3)
    private BigDecimal width;

    @Column(name = "height", precision = 10, scale = 3)
    private BigDecimal height;

    @ManyToOne
    @JoinColumn(name = "base_unit_id")
    private Unit baseUnit;

    @Column(name = "minstocklevel")
    private Integer minStockLevel = 10;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

}
