package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.OrderCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderTrackingResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<BaseResponse<List<OrderResponseDTO>>> getAllOrders(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.getAllOrders(roleId, userId), "All orders retrieved successfully");
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponse<List<OrderResponseDTO>>> getOrdersByStatus(
            @PathVariable com.overcode250204.smartlogicticssystem.enums.OrderStatus status,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.getOrdersByStatus(status, roleId, userId), "Orders retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDTO>> getOrderById(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.getOrderById(id, roleId, userId), "Order retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDTO>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.updateOrder(id, request, roleId, userId), "Order updated successfully");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BaseResponse<OrderResponseDTO>> cancelOrder(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.cancelOrder(id, roleId, userId), "Order cancelled successfully");
    }

    @GetMapping("/my-orders")
    public ResponseEntity<BaseResponse<List<OrderResponseDTO>>> getMyOrders(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.getMyOrders(roleId, userId), "My orders retrieved successfully");
    }

    @GetMapping("/my-orders/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDTO>> getMyOrderById(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.getMyOrderById(id, roleId, userId), "My order retrieved successfully");
    }

    @GetMapping("/my-orders/{id}/tracking")
    public ResponseEntity<BaseResponse<List<OrderTrackingResponseDTO>>> getMyOrderTracking(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.getMyOrderTracking(id, roleId, userId), "My order tracking retrieved successfully");
    }
}
