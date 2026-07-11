package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.LocalTripDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalTripDetailRepository extends JpaRepository<LocalTripDetail, Long> {
    List<LocalTripDetail> findByLocalTrip_LocalTripIdOrderByStopOrderAsc(Long localTripId);

    List<LocalTripDetail> findByLocalTrip_LocalTripId(Long localTripId);
}
