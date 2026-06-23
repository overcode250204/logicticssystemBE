package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.OrderCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;

public interface IOrderService {
    OrderResponseDTO createOrder(OrderCreateRequest request, int roleId, int userId);
}
