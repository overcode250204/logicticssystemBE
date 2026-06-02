package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.entities.Notification;
import com.overcode250204.smartlogicticssystem.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<Notification>>> getNotificationsByQuery(
            @RequestParam Long recipientId
    ) {
        return success(notificationService.getNotifications(recipientId), "Notifications retrieved successfully");
    }

    @GetMapping("/{recipientId}")
    public ResponseEntity<BaseResponse<List<Notification>>> getNotifications(@PathVariable Long recipientId) {
        return success(notificationService.getNotifications(recipientId), "Notifications retrieved successfully");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponse<Long>> countUnreadByQuery(
            @RequestParam Long recipientId
    ) {
        return success(notificationService.countUnread(recipientId), "Unread notification count retrieved successfully");
    }

    @GetMapping("/{recipientId}/unread-count")
    public ResponseEntity<BaseResponse<Long>> countUnread(@PathVariable Long recipientId) {
        return success(notificationService.countUnread(recipientId), "Unread notification count retrieved successfully");
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<BaseResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return success(null, "Notification marked as read successfully");
    }
}
