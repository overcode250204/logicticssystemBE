package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByZone_ZoneIdAndStatus(Long zoneId, DriverStatus status);
}
