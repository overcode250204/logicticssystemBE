package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductPageResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/page")
    public ResponseEntity<BaseResponse<ProductPageResponseDTO>> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(
                productService.getProductsPage(page, size, keyword, categoryId, supplierId, sortBy, sortDirection),
                "Page products retrieved successfully"
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<ProductResponseDTO>> createJson(@Valid @RequestBody ProductCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.create(request, roleId, userId), "Create successfully");
    }

    @Operation(
            summary = "Create product with optional image",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProductCreateMultipartRequest.class)
                    )
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<ProductResponseDTO>> create(
            @Parameter(description = "ProductCreateRequest JSON")
            @Valid @RequestPart("data") ProductCreateRequest request,
            @Parameter(description = "Optional product image file")
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.create(request, image, roleId, userId), "Create successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponseDTO>> getById(@PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.getById(id, roleId, userId), "Get by id successfully");
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<ProductResponseDTO>> updateJson(@PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.update(id, request, roleId, userId), "Update successfully");
    }

    @Operation(
            summary = "Update product with optional image replacement/removal",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProductUpdateMultipartRequest.class)
                    )
            )
    )
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<ProductResponseDTO>> update(@PathVariable Long id,
            @Parameter(description = "ProductUpdateRequest JSON")
            @Valid @RequestPart("data") ProductUpdateRequest request,
            @Parameter(description = "Optional new product image file")
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(productService.update(id, request, image, removeImage, roleId, userId), "Update successfully");
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

    public static class ProductCreateMultipartRequest {
        @Schema(implementation = ProductCreateRequest.class)
        public ProductCreateRequest data;

        @Schema(type = "string", format = "binary", nullable = true)
        public MultipartFile image;
    }

    public static class ProductUpdateMultipartRequest {
        @Schema(implementation = ProductUpdateRequest.class)
        public ProductUpdateRequest data;

        @Schema(type = "string", format = "binary", nullable = true)
        public MultipartFile image;

        @Schema(defaultValue = "false")
        public Boolean removeImage;
    }
}
