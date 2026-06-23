package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_categories")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryid")
    private Long categoryId;

    @Column(name = "categorycode", nullable = false, unique = true, length = 50)
    private String categoryCode;

    @Column(name = "categoryname", nullable = false, length = 100)
    private String categoryName;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = false
    )
    private List<Product> products = new ArrayList<>();

    @Column(name = "description", length = 500)
    private String description;
}