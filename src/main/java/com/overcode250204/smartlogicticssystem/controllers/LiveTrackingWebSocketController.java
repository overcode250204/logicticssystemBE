package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.ActiveVehicleInfo;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.enums.DriverRole;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripRepository;
import com.overcode250204.smartlogicticssystem.services.LiveTrackingCache;
import com.overcode250204.smartlogicticssystem.vrp.OsrmRoutingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LiveTrackingWebSocketController extends BaseController {

    private final LiveTrackingCache liveTrackingCache;
    private final LinehaulTripRepository linehaulTripRepository;
    private final OsrmRoutingService osrmRoutingService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/live-tracking/active-vehicles")
    public ResponseEntity<BaseResponse<List<ActiveVehicleInfo>>> getActiveVehicles() {
        return success(liveTrackingCache.getAll(), "Active vehicles retrieved successfully");
    }

    @MessageMapping("/track-location")
    public void trackLocation(LocationUpdatePayload payload) {
        if (payload == null || payload.getTrip_code() == null) {
            return;
        }

        log.info("Received location update for trip: {}, lat: {}, lng: {}", payload.getTrip_code(), payload.getLat(), payload.getLng());

        ActiveVehicleInfo info = liveTrackingCache.get(payload.getTrip_code());

        // Fetch-join query: drivers / routeConfig / toWarehouse are initialized here,
        // so nothing is a lazy proxy once the session is closed.
        LinehaulTrip trip = linehaulTripRepository
                .findByLinehaulTripCodeForTracking(payload.getTrip_code())
                .orElse(null);

        if (info == null) {
            if (trip != null) {
                String shipperName = "Nguyễn Văn A";
                if (trip.getTripDrivers() != null) {
                    for (LinehaulTripDriver tripDriver : trip.getTripDrivers()) {
                        if (tripDriver.getDriver() != null && DriverRole.MAIN.equals(tripDriver.getRole())) {
                            shipperName = tripDriver.getDriver().getName();
                            break;
                        }
                    }
                }

                LocalDateTime depTime = trip.getDepartureTime();
                if (depTime == null) {
                    depTime = LocalDateTime.now();
                }
                int slaHours = 24;
                if (trip.getRouteConfig() != null && trip.getRouteConfig().getSlaHours() != null) {
                    slaHours = trip.getRouteConfig().getSlaHours();
                }
                LocalDateTime deadlineTime = depTime.plusHours(slaHours);
                String formattedDeadline = deadlineTime.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

                info = ActiveVehicleInfo.builder()
                        .trip_code(trip.getLinehaulTripCode())
                        .shipper_name(shipperName)
                        .deadline(formattedDeadline)
                        .lat(payload.getLat())
                        .lng(payload.getLng())
                        .status("gray")
                        .build();
            } else {
                log.warn("Trip code {} not found in system.", payload.getTrip_code());
                return;
            }
        }

        String status = "gray";
        if (trip != null && trip.getRouteConfig() != null && trip.getRouteConfig().getToWarehouse() != null) {
            Warehouse toWarehouse = trip.getRouteConfig().getToWarehouse();
            if (toWarehouse.getLocation() != null && payload.getLat() != null && payload.getLng() != null) {
                double destLat = toWarehouse.getLocation().getY();
                double destLng = toWarehouse.getLocation().getX();

                // Call OSRM service to get expected duration in seconds
                double durationSeconds = osrmRoutingService.getTravelDurationSeconds(payload.getLat(), payload.getLng(), destLat, destLng);
                
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime eta = now.plusSeconds((long) durationSeconds);

                try {
                    LocalDateTime deadlineTime = LocalDateTime.parse(info.getDeadline(), DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
                    if (eta.isBefore(deadlineTime)) {
                        status = "green";
                    } else {
                        status = "yellow";
                    }
                } catch (Exception e) {
                    log.error("Failed to parse deadline time: {}", info.getDeadline(), e);
                    status = "green"; // Fallback to green
                }
            }
        }

        // Update the cached data
        info.setLat(payload.getLat());
        info.setLng(payload.getLng());
        info.setStatus(status);
        info.setLast_ping_time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

        liveTrackingCache.put(payload.getTrip_code(), info);

        // Broadcast to admin room
        messagingTemplate.convertAndSend("/topic/admin_room", info);
        log.info("Broadcasted tracking update to /topic/admin_room: {}", info);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationUpdatePayload {
        private String trip_code;
        private Double lat;
        private Double lng;
    }
}
