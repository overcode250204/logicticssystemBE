package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;

import com.overcode250204.smartlogicticssystem.base.BaseResponse;

import com.overcode250204.smartlogicticssystem.dtos.request.order.OrderRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.order.OrderResponseDTO;

import com.overcode250204.smartlogicticssystem.services.impls.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<BaseResponse<OrderResponseDTO>> create(@Valid @RequestBody OrderRequest request,
                                                                 @RequestHeader(name = "X-Role-Id") int roleId,
                                                                    @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.create(request,roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDTO>> getById(@PathVariable Long id,
                                                              @RequestHeader(name = "X-Role-Id") int roleId,
                                                              @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDTO>> update(@PathVariable Long id,
                                                         @Valid @RequestBody OrderRequest request,
                                                         @RequestHeader(name = "X-Role-Id") int roleId,
                                                         @RequestHeader(name = "X-User-Id") int userId) {
        return success(orderService.update(id, request, roleId, userId), "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                     @RequestHeader(name = "X-User-Id") int userId) {
        orderService.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }
}
