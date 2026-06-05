package com.overcode250204.smartlogicticssystem.controllers;


import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.delivery.OrderRequestDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.services.impls.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.overcode250204.smartlogicticssystem.base.BaseResponse.success;

@RestController
@RequestMapping("/api/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final DeliveryService service;

    @PostMapping
    public ResponseEntity<BaseResponse<OrderRequestDTO>> create(@Valid @RequestBody OrderRequestDTO request) {
        return success(service.placeOrder(request), "Create successfully");
    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<BaseResponse<ProductResponseDTO>> getById(@PathVariable Long id,
//                                                                    @RequestHeader(name = "X-Role-Id") int roleId,
//                                                                    @RequestHeader(name = "X-User-Id") int userId) {
//        return success(productService.getById(id, roleId, userId), "Get by id successfully");
//    }
}
