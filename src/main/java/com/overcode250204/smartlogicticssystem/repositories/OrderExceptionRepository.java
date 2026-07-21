package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.OrderException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface OrderExceptionRepository extends JpaRepository<OrderException, Long> {

    boolean existsByOrder_OrderIdAndExceptionReason_ReasonId(Long orderId, Long reasonId);

    @Query("""
    SELECT COUNT(e)
    FROM OrderException e
    WHERE e.createdAt >= :from AND e.createdAt < :to
    """)
    Long countCriticalAlerts(LocalDateTime from,
                             LocalDateTime to);
}
