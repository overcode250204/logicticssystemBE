package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.InventoryDeductedEvent;

public interface NotificationRealtimeService {
    void sendInventoryDeductedNotification(
            InventoryDeductedEvent event
            );

    /**
     * Tạo + đẩy realtime thông báo "pallet sẵn sàng để quét" tới một staff.
     */
    void sendPalletizationTaskNotification(Long recipientId, String palletCode);
}
