package com.overcode250204.smartlogicticssystem.entities;

import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_code", unique = true, length = 20)
    private String orderCode;

    @Column(name = "barcode_url")
    private String barcodeUrl;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "delivery_address", columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_province", nullable = false, length = 50)
    private String deliveryProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_hub_id")
    private Warehouse assignedHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private RouteConfig routeConfig;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 20)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private OrderStatus status = OrderStatus.NEW;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "delivery_point", columnDefinition = "geometry(Point, 4326)")
    private Point deliveryPoint;

    @Column(name = "total_weight_kg", nullable = false)
    private BigDecimal totalWeightKg;

    @Column(name = "total_volume_m3", nullable = false)
    private BigDecimal totalVolumeM3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @Column(name = "proof_url", length = 500)
    private String proofUrl;
}