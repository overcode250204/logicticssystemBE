package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDeductedEvent;
import com.overcode250204.smartlogicticssystem.services.NotificationRealtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test/notifications")
@Slf4j
@RequiredArgsConstructor
public class TestNotificationController extends BaseController {

    private final NotificationRealtimeService notificationRealtimeService;

    @PostMapping("/inventory-deducted")
    public ResponseEntity<BaseResponse<Void>> sendInventoryDeductedNotification(
            @RequestBody(required = false) InventoryDeductedEvent request
    ) {
        InventoryDeductedEvent event = request != null
                ? request
                : new InventoryDeductedEvent(
                        1L,
                        "Bao",
                        "Nike Shoes",
                        5,
                        LocalDateTime.now()
                );

        notificationRealtimeService.sendInventoryDeductedNotification(event);
        log.info("Sent test inventory deducted notification for recipientId={}", event.recipientId());

        return success(null, "Test inventory deducted notification sent successfully");
    }
}
