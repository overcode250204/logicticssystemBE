package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.InventoryDeductedEvent;
import com.overcode250204.smartlogicticssystem.entities.Notification;
import com.overcode250204.smartlogicticssystem.services.NotificationRealtimeService;
import com.overcode250204.smartlogicticssystem.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRealtimeServiceImpl implements NotificationRealtimeService {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendInventoryDeductedNotification(InventoryDeductedEvent event) {
        Notification notification = notificationService.createInventoryDeductedNotification(
                event.recipientId(),
                event.staffName(),
                event.productName(),
                event.quantity()
        );
        String topic = "/topic/notifications/" + event.recipientId();
        messagingTemplate.convertAndSend(topic, notification);

        log.info(
                "Sent inventory deducted notification successfully. recipientId={}, notificationId={}, topic={}",
                event.recipientId(),
                notification.getId(),
                topic
        );
    }

    @Override
    public void sendPalletizationTaskNotification(Long recipientId, String palletCode) {
        Notification notification =
                notificationService.createPalletizationTaskNotification(recipientId, palletCode);
        String topic = "/topic/notifications/" + recipientId;
        messagingTemplate.convertAndSend(topic, notification);

        log.info(
                "Sent palletization task notification successfully. recipientId={}, notificationId={}, palletCode={}, topic={}",
                recipientId,
                notification.getId(),
                palletCode,
                topic
        );
    }
}
