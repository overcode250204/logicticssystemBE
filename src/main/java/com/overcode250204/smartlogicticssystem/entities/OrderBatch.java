package com.overcode250204.smartlogicticssystem.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "orderbatchs")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderbatchid")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipperid")
    private Shipper shipper; // Shipper được gán cụm đơn gom này

    @Column(length = 30)
    private String status = "ASSIGNED"; // ASSIGNED, PACKING, PICKED_UP, COMPLETED

    @Column(name = "optimizedat")
    private LocalDateTime optimizedAt; // Thời điểm thuật toán chạy tối ưu sắp xếp

    @CreationTimestamp
    @Column(name = "createdat", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC") // Tự động sắp xếp đúng theo thứ tự hệ thống đề xuất khi gọi getRoutePoints()
    private List<RoutePoint> routePoints;
}
