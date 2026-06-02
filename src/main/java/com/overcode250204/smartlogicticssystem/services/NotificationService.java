package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.entities.Notification;

import java.util.List;

public interface NotificationService {
    Notification createInventoryDeductedNotification(
            Long recipientId,
            String staffName,
            String productName,
            int quantity
    );

    List<Notification> getNotifications(Long recipientId);

    long countUnread(Long recipientId);

    void markAsRead(Long notificationId);
}
