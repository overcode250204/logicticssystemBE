package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.OrderCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;

import java.util.List;

import com.overcode250204.smartlogicticssystem.enums.OrderStatus;

public interface IOrderService {
    OrderResponseDTO createOrder(OrderCreateRequest request, int roleId, int userId);
    List<OrderResponseDTO> getAllOrders(int roleId, int userId);
    List<OrderResponseDTO> getOrdersByStatus(OrderStatus status, int roleId, int userId);
    OrderResponseDTO getOrderById(Long id, int roleId, int userId);
    OrderResponseDTO updateOrder(Long id, OrderCreateRequest request, int roleId, int userId);
    OrderResponseDTO cancelOrder(Long id, int roleId, int userId);
}
