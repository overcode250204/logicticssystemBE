package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "routepoints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoutePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routepointid")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderbatchid", nullable = false)
    private OrderBatch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderid")
    private Order order;

    @Column(name = "sequencenumber", nullable = false)
    private Integer sequenceNumber; // 1, 2, 3... Thứ tự di chuyển hiển thị lên App Shipper
//
//    @Column(name = "estimatedarrivaltime")
//    private LocalDateTime estimatedArrivalTime; // Thời gian dự kiến đến (ETA)

//    @Column(name = "actualarrivaltime")
//    private LocalDateTime actualArrivalTime; // Lưu lại khi shipper bấm nút "Đã đến nơi" trên App

    @Column(length = 30)
    private String status = "TODO"; // TODO (Chưa đi), ARRIVED (Đã đến), COMPLETED (Xong), FAILED (Lỗi)

    @UpdateTimestamp
    @Column(name = "updatedat")
    private LocalDateTime updatedAt;
}