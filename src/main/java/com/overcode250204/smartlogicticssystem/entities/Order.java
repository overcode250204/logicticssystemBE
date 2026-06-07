package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User customer;

    @Column(name = "totalamount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "totalweight", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalWeight;

    @Column(name = "destinationaddress", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    // Tọa độ GPS chính xác vị trí nhà khách hàng (Kiểu Point của JTS)
    @Column(name = "deliverylocation", columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point deliveryLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zoneid")
    private Zone zone; // Thuộc vùng phân vùng nào

    @Column(nullable = false, length = 50)
    private String status = "PENDING"; // PENDING, CONFIRMED, IN_TRANSIT, DELIVERED, COMPLETED, CANCELLED, FAILED

    @CreationTimestamp
    @Column(name = "createdat", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updateat")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}