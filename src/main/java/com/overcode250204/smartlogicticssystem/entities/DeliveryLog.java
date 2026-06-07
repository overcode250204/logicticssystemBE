package com.overcode250204.smartlogicticssystem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliverylogs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderid")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipperid")
    private DriverProfile driverProfile;

    @Column(name = "fromstatus", length = 50)
    private String fromStatus; // Trạng thái trước khi đổi

    @Column(name = "tostatus", length = 50)
    private String toStatus; // Trạng thái sau khi đổi

    @Column(name = "logtype", length = 30)
    private String logType; // STATUS_CHANGE (Đổi trạng thái), INCIDENT (Sự cố dọc đường)

    @Column(columnDefinition = "TEXT")
    private String note; // Trường để shipper nhập lý do khi bùng hàng / hỏng xe / giao thất bại

    @Column(name = "imageurl", length = 255)
    private String imageUrl; // Đường dẫn ảnh chụp bằng chứng giao thất bại / hoàn hàng

    @CreationTimestamp
    @Column(name = "createdat", updatable = false)
    private LocalDateTime createdAt;
}