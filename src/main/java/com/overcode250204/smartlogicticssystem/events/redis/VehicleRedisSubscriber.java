package com.overcode250204.smartlogicticssystem.events.redis;

import com.overcode250204.smartlogicticssystem.entities.VehicleLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleRedisSubscriber {

    private final SimpMessagingTemplate messagingTemplate;

    public void receive(VehicleLocation location) {

        messagingTemplate.convertAndSend(
                "/topic/vehicles",
                location
        );
    }
}