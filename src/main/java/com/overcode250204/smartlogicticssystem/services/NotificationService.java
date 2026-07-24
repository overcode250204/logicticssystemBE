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

    /**
     * Thông báo cho staff biết một pallet đã sẵn sàng để quét đơn (status CAN_SEAL).
     */
    Notification createPalletizationTaskNotification(
            Long recipientId,
            String palletCode
    );

    List<Notification> getNotifications(Long recipientId);

    List<Notification> getNotifications(Long recipientId, Boolean isRead);

    long countUnread(Long recipientId);

    void markAsRead(Long notificationId);
}
