package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.entities.VehicleLocation;
import com.overcode250204.smartlogicticssystem.events.redis.VehicleRedisPublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRedisPublisher publisher;

    private final List<VehicleLocation> vehicles
            = new ArrayList<>();

    private final Random random = new Random();

    @PostConstruct
    void init() {
        System.out.println("hihi");
        for (int i = 0; i < 100; i++) {

            vehicles.add(
                    new VehicleLocation(
                            "VH_" + i,
                            10.7769 + random.nextDouble() * 0.05,
                            106.7009 + random.nextDouble() * 0.05,
                            random.nextDouble() * 360
                    )
            );
        }
    }

    @Scheduled(fixedRate = 1000)
    public void moveVehicles() {
        vehicles.forEach(v -> {

            v.setLat(
                    v.getLat()
                            + (random.nextDouble() - 0.5) * 0.001
            );

            v.setLng(
                    v.getLng()
                            + (random.nextDouble() - 0.5) * 0.001
            );

            publisher.publish(v);
        });
    }
}