package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            SELECT COALESCE(SUM(totalamount), 0)
            FROM orders
            WHERE UPPER(orderstatus) = UPPER(:status)
            """, nativeQuery = true)
    BigDecimal sumTotalAmountByStatus(@Param("status") String status);

    @Query(value = """
            SELECT COALESCE(SUM(totalamount), 0)
            FROM orders
            WHERE UPPER(orderstatus) = UPPER(:status)
              AND createdat >= :fromDateTime
              AND createdat < :toDateTime
            """, nativeQuery = true)
    BigDecimal sumTotalAmountByStatusAndCreatedAtRange(
            @Param("status") String status,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime
    );

}
