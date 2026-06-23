package com.overcode250204.smartlogicticssystem.scheduler;

import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
import com.overcode250204.smartlogicticssystem.services.IRoutingEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutingScheduler {

    private final RouteConfigRepository routeConfigRepository;
    private final IRoutingEngineService routingEngineService;

    // Run every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void scheduleRoutingChecks() {
        log.info("Running scheduled routing checks...");
        List<RouteConfig> activeRoutes = routeConfigRepository.findAll();
        for (RouteConfig route : activeRoutes) {
            try {
                routingEngineService.checkRoutingCondition(route.getRouteId());
            } catch (Exception e) {
                log.error("Error evaluating route conditions for route: " + route.getRouteId(), e);
            }
        }
    }
}
