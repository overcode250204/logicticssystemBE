package com.overcode250204.smartlogicticssystem.configs;

import com.overcode250204.smartlogicticssystem.entities.VehicleLocation;
import com.overcode250204.smartlogicticssystem.events.redis.VehicleRedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.*;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final VehicleRedisSubscriber subscriber;

    @Bean
    RedisMessageListenerContainer container(
            RedisConnectionFactory factory) {

        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();

        container.setConnectionFactory(factory);

        MessageListenerAdapter adapter =
                new MessageListenerAdapter(
                        subscriber,
                        "receive"
                );

        Jackson2JsonRedisSerializer<VehicleLocation> serializer =
                new Jackson2JsonRedisSerializer<>(VehicleLocation.class);
        adapter.setSerializer(serializer);
        adapter.afterPropertiesSet();

        container.addMessageListener(
                adapter,
                new PatternTopic("vehicle-location")
        );

        return container;
    }
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {

        RedisTemplate<String, Object> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<VehicleLocation> serializer =
                new Jackson2JsonRedisSerializer<>(VehicleLocation.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
}
