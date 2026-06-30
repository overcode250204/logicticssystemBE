package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.LocalTrip;
import com.overcode250204.smartlogicticssystem.enums.LocalTripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalTripRepository extends JpaRepository<LocalTrip, Long> {
    List<LocalTrip> findByStatus(LocalTripStatus status);
}
