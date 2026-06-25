package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.ProductCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.UnitResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final SupplierMapper supplierMapper;
    private final ProductCategoryMapper productCategoryMapper;

    public ProductResponseDTO toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .sku(product.getSku())
                .price(product.getPrice())
                .weight(product.getWeight())
                .length(product.getLength())
                .width(product.getWidth())
                .height(product.getHeight())
                .baseUnit(toUnitResponse(product.getBaseUnit()))
                .minStockLevel(product.getMinStockLevel())
                .supplier(supplierMapper.toSimpleResponse(product.getSupplier()))
                .category(productCategoryMapper.toResponse(product.getCategory()))
                .imageUrl(product.getImageUrl())
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
                .sku(product.getSku())
                .minStockLevel(product.getMinStockLevel())
                .weight(product.getWeight())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }

    public Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setSku(request.getSku());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setPrice(request.getPrice());
        product.setWeight(request.getWeight());
        product.setLength(request.getLength());
        product.setWidth(request.getWidth());
        product.setHeight(request.getHeight());
        return product;
    }

    public void updateEntity(ProductUpdateRequest request, Product entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setProductCode(request.getProductCode());
        entity.setProductName(request.getProductName());
        entity.setSku(request.getSku());
        entity.setMinStockLevel(request.getMinStockLevel());
        entity.setWeight(request.getWeight());
        entity.setLength(request.getLength());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setPrice(request.getPrice());
    }

    private UnitResponseDTO toUnitResponse(Unit unit) {
        if (unit == null) return null;
        return UnitResponseDTO.builder()
                .id(unit.getId())
                .code(unit.getCode())
                .name(unit.getName())
                .type(unit.getType())
                .build();
    }
}
