package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.enums.VehicleType;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);

    long countByStatusAndVehicleTypeIn(VehicleStatus status, Collection<VehicleType> vehicleTypes);

    @Query("""
    SELECT v.status, COUNT(v)
    FROM Vehicle v
    GROUP BY v.status
    """)
    List<Object[]> countVehiclesByStatus();
}
