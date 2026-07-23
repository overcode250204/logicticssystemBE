package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.OrderException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderExceptionRepository extends JpaRepository<OrderException, Long> {

    boolean existsByOrder_OrderIdAndExceptionReason_ReasonId(Long orderId, Long reasonId);

    @Query("""
    SELECT COUNT(e)
    FROM OrderException e
    WHERE e.createdAt >= :from AND e.createdAt < :to
    """)
    Long countCriticalAlerts(LocalDateTime from,
                             LocalDateTime to);

    @Query("""
    SELECT e
    FROM OrderException e
    LEFT JOIN FETCH e.order o
    LEFT JOIN FETCH e.exceptionReason r
    ORDER BY e.createdAt DESC
    """)
    List<OrderException> findRecentExceptions(Pageable pageable);
}
