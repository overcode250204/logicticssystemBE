package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductCategoryResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryController extends BaseController {

    private final IProductCategoryService productCategoryService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductCategoryResponseDTO>>> getAllCategories() {
        return success(productCategoryService.getAllCategories(), "All categories retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ProductCategoryResponseDTO>> create(@Valid @RequestBody ProductCategoryCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productCategoryService.create(request, roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductCategoryResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productCategoryService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductCategoryResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody ProductCategoryUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productCategoryService.update(id, request, roleId, userId), "Update successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        productCategoryService.delete(id, roleId, userId);
        return success(null, "Delete successfully");
    }

    @GetMapping("/code/{categoryCode}")
    public ResponseEntity<BaseResponse<ProductCategoryResponseDTO>> getByCategoryCode(@PathVariable String categoryCode) {
        return success(productCategoryService.getByCategoryCode(categoryCode), "Category retrieved successfully");
    }
}


