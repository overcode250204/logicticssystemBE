package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteConfigRepository extends JpaRepository<RouteConfig, Long> {
    Optional<RouteConfig> findByRouteNameIgnoreCase(String routeName);
}
