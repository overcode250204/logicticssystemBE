package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRouteConfigAndStatusOrderByCreatedAtAsc(RouteConfig routeConfig, OrderStatus status);
    boolean existsByOrderCode(String orderCode);
}
