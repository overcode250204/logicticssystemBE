package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface LinehaulTripRepository extends JpaRepository<LinehaulTrip, Long> {
    boolean existsByLinehaulTripCode(String linehaulTripCode);
    java.util.Optional<LinehaulTrip> findByLinehaulTripCode(String linehaulTripCode);
    List<LinehaulTrip> findByStatus(com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus status);

    /**
     * Load a trip with everything the live-tracking handler touches, so no lazy
     * proxy is dereferenced after the persistence session is closed.
     */
    @Query("""
    SELECT DISTINCT t FROM LinehaulTrip t
    LEFT JOIN FETCH t.tripDrivers td
    LEFT JOIN FETCH td.driver
    LEFT JOIN FETCH t.routeConfig rc
    LEFT JOIN FETCH rc.toWarehouse
    WHERE t.linehaulTripCode = :linehaulTripCode
    """)
    java.util.Optional<LinehaulTrip> findByLinehaulTripCodeForTracking(@Param("linehaulTripCode") String linehaulTripCode);

    @Query("SELECT t FROM LinehaulTrip t WHERE YEAR(t.departureTime) = :year AND MONTH(t.departureTime) = :month")
    List<LinehaulTrip> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT t FROM LinehaulTrip t WHERE YEAR(t.departureTime) = :year")
    List<LinehaulTrip> findByYear(@Param("year") int year);

    @Query("SELECT t FROM LinehaulTrip t WHERE " +
           "(:year IS NULL OR YEAR(t.departureTime) = :year) AND " +
           "(:month IS NULL OR MONTH(t.departureTime) = :month) AND " +
           "(:searchKeyword IS NULL OR " +
           "LOWER(t.linehaulTripCode) LIKE :searchKeyword OR " +
           "LOWER(t.vehicle.licensePlate) LIKE :searchKeyword OR " +
           "LOWER(t.status) LIKE :searchKeyword OR " +
           "EXISTS (SELECT td FROM LinehaulTripDriver td WHERE td.linehaulTrip = t AND LOWER(td.driver.name) LIKE :searchKeyword))")
    Page<LinehaulTrip> searchLinehaulTrips(@Param("year") Integer year, 
                                          @Param("month") Integer month, 
                                          @Param("searchKeyword") String searchKeyword, 
                                          Pageable pageable);

    @Query("SELECT t FROM LinehaulTrip t WHERE t.status = 'EN_ROUTE' ORDER BY t.departureTime ASC")
    List<LinehaulTrip> findDelayedTrips(Pageable pageable);

    @Query("""
    SELECT t.status, COUNT(t)
    FROM LinehaulTrip t
    GROUP BY t.status
    """)
    List<Object[]> countLinehaulTripsByStatus();
}
