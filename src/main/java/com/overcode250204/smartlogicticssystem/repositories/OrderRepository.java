package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRouteConfigAndStatusOrderByCreatedAtAsc(RouteConfig routeConfig, OrderStatus status);
    boolean existsByOrderCode(String orderCode);
    java.util.Optional<Order> findByOrderCode(String orderCode);
    List<Order> findByZone_ZoneIdAndStatusIn(Long zoneId, List<OrderStatus> statuses);
    List<Order> findByStatus(OrderStatus status, org.springframework.data.domain.Sort sort);

    @Query("""
    SELECT COUNT(o)
    FROM Order o
    WHERE o.createdAt BETWEEN :from AND :to
    """)
    Long countOrders(LocalDateTime from,
                     LocalDateTime to);

    @Query("""
    SELECT COUNT(o)
    FROM Order o
    WHERE o.actualDeliveryTime BETWEEN :from AND :to
    AND o.status = OrderStatus.DELIVERED
    """)
    Long countDeliveredOrders(LocalDateTime from,
                              LocalDateTime to);

    @Query("""
    SELECT COUNT(o)
    FROM Order o
    WHERE o.actualDeliveryTime BETWEEN :from AND :to
    AND o.status IN (
    OrderStatus.DELIVERED,
    OrderStatus.FAILED
    )
    """)
    Long countFinishedOrders(LocalDateTime from,
                             LocalDateTime to);
}
