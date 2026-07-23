package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByPhone(String phone);

    /**
     * Khoá ghi bi quan (SELECT ... FOR UPDATE) trên hàng Driver. Dùng để tuần tự
     * hoá hai giao dịch cùng gán một tài xế: giao dịch thứ hai phải chờ giao dịch
     * đầu commit, sau đó mới đọc active assignment (đã có) và bị chặn.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.driverId = :id")
    Optional<Driver> findByIdForUpdate(@Param("id") Long id);

    List<Driver> findByZone_ZoneIdAndStatus(Long zoneId, DriverStatus status);


    @org.springframework.data.jpa.repository.Query("SELECT d.name, COUNT(ltd) FROM LocalTripDetail ltd JOIN ltd.localTrip lt JOIN lt.driver d WHERE ltd.status = 'COMPLETED' AND (:startDate IS NULL OR lt.createdAt >= :startDate) AND (:endDate IS NULL OR lt.createdAt <= :endDate) GROUP BY d.name ORDER BY COUNT(ltd) DESC")
    java.util.List<Object[]> getShipperLeaderboard(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate, org.springframework.data.domain.Pageable pageable);
    Optional<Driver> findByUser_UserId(Long userId);
}
