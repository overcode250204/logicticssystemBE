package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    Optional<DriverProfile> findByUserId(Long userId);
}
