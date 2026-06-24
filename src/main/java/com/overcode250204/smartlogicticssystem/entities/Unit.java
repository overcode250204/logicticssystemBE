package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.UnitType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "units")
@Getter
@Setter
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "units_id")
    private Long id;

    @Column(nullable = false, unique = true, name = "unit_code", length = 20)

    private String code; // KG, G, CM, M, PCS

    @Column(nullable = false, name = "unit_name", length = 20)
    private String name; // Kilogram, Gram,...

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", length = 20)
    private UnitType type;
}