package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.LocalTrip;
import com.overcode250204.smartlogicticssystem.enums.LocalTripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LocalTripRepository extends JpaRepository<LocalTrip, Long> {
    List<LocalTrip> findByStatus(LocalTripStatus status);
    List<LocalTrip> findByDriver_DriverId(Long driverId);
    boolean existsByLocalTripCode(String localTripCode);

    @Query("SELECT t FROM LocalTrip t WHERE YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month")
    List<LocalTrip> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT t FROM LocalTrip t WHERE YEAR(t.createdAt) = :year")
    List<LocalTrip> findByYear(@Param("year") int year);

    @Query("SELECT t FROM LocalTrip t WHERE " +
           "(:year IS NULL OR YEAR(t.createdAt) = :year) AND " +
           "(:month IS NULL OR MONTH(t.createdAt) = :month) AND " +
           "(:searchKeyword IS NULL OR " +
           "LOWER(t.localTripCode) LIKE :searchKeyword OR " +
           "LOWER(t.vehicle.licensePlate) LIKE :searchKeyword OR " +
           "LOWER(t.status) LIKE :searchKeyword OR " +
           "LOWER(t.driver.name) LIKE :searchKeyword)")
    Page<LocalTrip> searchLocalTrips(@Param("year") Integer year, 
                                    @Param("month") Integer month, 
                                    @Param("searchKeyword") String searchKeyword, 
                                    Pageable pageable);

    @Query("""
    SELECT t.status, COUNT(t)
    FROM LocalTrip t
    GROUP BY t.status
    """)
    List<Object[]> countLocalTripsByStatus();
}
