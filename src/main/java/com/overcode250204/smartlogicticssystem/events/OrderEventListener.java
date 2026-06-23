package com.overcode250204.smartlogicticssystem.events;

import com.overcode250204.smartlogicticssystem.services.IRoutingEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final IRoutingEngineService routingEngineService;

    @Async
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for routeId: {}", event.getRouteId());
        try {
            routingEngineService.checkRoutingCondition(event.getRouteId());
        } catch (Exception e) {
            log.error("Failed to process routing conditions for newly created order on route: " + event.getRouteId(), e);
        }
    }
}
