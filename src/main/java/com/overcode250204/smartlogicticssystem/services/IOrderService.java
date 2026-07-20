package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.OrderCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;

import java.util.List;

import com.overcode250204.smartlogicticssystem.enums.OrderStatus;

import com.overcode250204.smartlogicticssystem.dtos.response.OrderTrackingResponseDTO;

public interface IOrderService {
    OrderResponseDTO createOrder(OrderCreateRequest request, int roleId, int userId);
    List<OrderResponseDTO> getAllOrders(int roleId, int userId);
    List<OrderResponseDTO> getOrdersByStatus(OrderStatus status, int roleId, int userId);
    OrderResponseDTO getOrderById(Long id, int roleId, int userId);
    OrderResponseDTO updateOrder(Long id, OrderCreateRequest request, int roleId, int userId);
    OrderResponseDTO cancelOrder(Long id, int roleId, int userId);
    List<OrderResponseDTO> getMyOrders(int roleId, int userId);
    OrderResponseDTO getMyOrderById(Long id, int roleId, int userId);
    List<OrderTrackingResponseDTO> getMyOrderTracking(Long orderId, int roleId, int userId);
}
