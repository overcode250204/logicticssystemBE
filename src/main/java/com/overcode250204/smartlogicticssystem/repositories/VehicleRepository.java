package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);

    @Query("""
    SELECT COUNT(v)
    FROM Vehicle v
    WHERE v.status =
    com.overcode250204.smartlogicticssystem.enums.VehicleStatus.ON_TRIP
    """)
    Long countActiveFleet();
}
