package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.InventoryDeductedEvent;

public interface NotificationRealtimeService {
    void sendInventoryDeductedNotification(
            InventoryDeductedEvent event
            );
}
