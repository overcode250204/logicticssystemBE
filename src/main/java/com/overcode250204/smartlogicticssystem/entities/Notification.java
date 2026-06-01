package com.overcode250204.smartlogicticssystem.entities;


import com.overcode250204.smartlogicticssystem.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private UUID recipientId;

    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime readAt;

    private UUID referenceId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}