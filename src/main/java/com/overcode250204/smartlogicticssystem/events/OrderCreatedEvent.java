package com.overcode250204.smartlogicticssystem.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    private final Long routeId;

    public OrderCreatedEvent(Object source, Long routeId) {
        super(source);
        this.routeId = routeId;
    }
}
