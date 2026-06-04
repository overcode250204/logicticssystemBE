package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.ProductCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final SupplierMapper supplierMapper;

    public ProductResponseDTO toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .price(product.getPrice())
                .minStockLevel(product.getMinStockLevel())
                .supplier(supplierMapper.toSimpleResponse(product.getSupplier()))
                .build();
    }

    public ProductResponseDTO toSimpleResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .minStockLevel(product.getMinStockLevel())
                .price(product.getPrice())
                .build();
    }

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setPrice(request.getPrice());
        return product;
    }

    public void updateEntity(ProductUpdateRequest request, Product entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setProductCode(request.getProductCode());
        entity.setProductName(request.getProductName());
        entity.setMinStockLevel(request.getMinStockLevel());
        entity.setPrice(request.getPrice());
    }
}
