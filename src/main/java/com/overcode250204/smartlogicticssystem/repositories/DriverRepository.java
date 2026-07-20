package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByZone_ZoneIdAndStatus(Long zoneId, DriverStatus status);
    java.util.Optional<Driver> findByUser_UserId(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT d.name, COUNT(ltd) FROM LocalTripDetail ltd JOIN ltd.localTrip lt JOIN lt.driver d WHERE ltd.status = 'COMPLETED' AND (:startDate IS NULL OR lt.createdAt >= :startDate) AND (:endDate IS NULL OR lt.createdAt <= :endDate) GROUP BY d.name ORDER BY COUNT(ltd) DESC")
    java.util.List<Object[]> getShipperLeaderboard(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate, org.springframework.data.domain.Pageable pageable);
}
