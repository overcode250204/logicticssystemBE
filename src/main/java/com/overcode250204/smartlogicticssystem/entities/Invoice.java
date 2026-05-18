package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoiceid")
    private Long invoiceId;

    @Column(name = "invoicetype", nullable = false, length = 50)
    private String invoiceType;

    @Column(name = "totalamount", nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby", nullable = false)
    private User createdBy;

    @Column(name = "createdat", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}