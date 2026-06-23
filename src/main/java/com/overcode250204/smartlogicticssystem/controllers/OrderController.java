package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.OrderCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<BaseResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.createOrder(request, roleId, userId), "Order created successfully");
    }
}
