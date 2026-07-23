package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.entities.Notification;
import com.overcode250204.smartlogicticssystem.enums.NotificationType;
import com.overcode250204.smartlogicticssystem.repositories.NotificationRepository;
import com.overcode250204.smartlogicticssystem.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createInventoryDeductedNotification(
            Long recipientId,
            String staffName,
            String productName,
            int quantity
    ) {
        Notification notification = new Notification();

        notification.setTitle("Inventory deducted");
        notification.setMessage("%s deducted %d item(s) of %s"
                .formatted(staffName, quantity, productName));

        notification.setType(NotificationType.INVENTORY_DEDUCTED);
        notification.setRecipientId(recipientId);

        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setReadAt(null);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotifications(Long recipientId) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Override
    public List<Notification> getNotifications(Long recipientId, Boolean isRead) {
        if (isRead == null) {
            return getNotifications(recipientId);
        }
        return notificationRepository
                .findByRecipientIdAndIsReadOrderByCreatedAtDesc(recipientId, isRead);
    }

    @Override
    public long countUnread(Long recipientId) {
        return notificationRepository
                .countByRecipientIdAndIsReadFalse(recipientId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (Boolean.TRUE.equals(notification.getIsRead())) {
            return;
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}