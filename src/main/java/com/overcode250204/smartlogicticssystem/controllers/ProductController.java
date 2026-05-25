package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController extends BaseController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductResponseDTO>>> getAllProducts() {
        return success(productService.getAllProducts(), "All products retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ProductResponseDTO>> create(@Valid @RequestBody ProductCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.create(request, roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.update(id, request, roleId, userId), "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        productService.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }

    @GetMapping("/code/{productCode}")
    public ResponseEntity<BaseResponse<ProductResponseDTO>> getByProductCode(@PathVariable String productCode) {
        return success(productService.getByProductCode(productCode), "Product retrieved successfully");
    }
}
