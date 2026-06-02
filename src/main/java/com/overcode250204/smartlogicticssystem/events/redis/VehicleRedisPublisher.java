package com.overcode250204.smartlogicticssystem.events.redis;


import com.overcode250204.smartlogicticssystem.entities.VehicleLocation;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VehicleRedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(VehicleLocation location){
        redisTemplate.convertAndSend("vehicle-location", location);
    }

}
