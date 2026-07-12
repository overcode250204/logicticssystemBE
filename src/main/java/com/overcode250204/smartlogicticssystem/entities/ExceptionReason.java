package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exception_reasons")
public class ExceptionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reason_id")
    private Long reasonId;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "reason_text", length = 255)
    private String reasonText;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
