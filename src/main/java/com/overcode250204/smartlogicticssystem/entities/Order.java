package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driverid")
    private User driver;

    @Column(name = "destinationaddress", nullable = false)
    private String destinationAddress;

    @Column(name = "orderstatus")
    private String orderStatus = "Pending";

    @Column(name = "totalweight")
    private BigDecimal totalWeight;

    @Column(name = "totalamount")
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "createdat", updatable = false)
    private LocalDateTime createdAt;
}
