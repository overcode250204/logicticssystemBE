package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.DriverShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverShiftRepository extends JpaRepository<DriverShift, Long> {
    List<DriverShift> getAllByDriverProfile_UserId(Long driverProfileUserId);
}
