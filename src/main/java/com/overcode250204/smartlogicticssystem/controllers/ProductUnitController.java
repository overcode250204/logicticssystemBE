package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUnitCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUnitUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductUnitResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IProductUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-units")
@RequiredArgsConstructor
public class ProductUnitController extends BaseController {

    private final IProductUnitService productUnitService;

    /**
     * Create a new unit conversion mapping for a product. Admin only (roleId = 1).
     */
    @PostMapping
    public ResponseEntity<BaseResponse<ProductUnitResponseDTO>> create(
            @Valid @RequestBody ProductUnitCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productUnitService.create(request, roleId, userId), "Product unit created successfully");
    }

    /**
     * Update the conversion factor for an existing product-unit mapping. Admin only.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductUnitResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUnitUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productUnitService.update(id, request, roleId, userId), "Product unit updated successfully");
    }

    /**
     * Get a specific product-unit mapping by its ID. Admin only.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductUnitResponseDTO>> getById(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productUnitService.getById(id, roleId, userId), "Product unit retrieved successfully");
    }

    /**
     * Delete a product-unit mapping. Admin only.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        productUnitService.delete(id, roleId, userId);
        return success(null, "Product unit deleted successfully");
    }

    /**
     * List all unit conversion mappings for a specific product. Admin only.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<BaseResponse<List<ProductUnitResponseDTO>>> getByProductId(
            @PathVariable Long productId,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productUnitService.getByProductId(productId, roleId, userId),
                "Product units retrieved successfully");
    }
}
