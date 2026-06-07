package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductCategoryResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.ProductCategory;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoryMapper {

    public ProductCategoryResponseDTO toResponse(ProductCategory category) {
        if (category == null) {
            return null;
        }

        return ProductCategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .build();
    }

    public ProductCategory toEntity(ProductCategoryCreateRequest request) {
        if (request == null) {
            return null;
        }

        ProductCategory category = new ProductCategory();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());

        return category;
    }

    public void updateEntity(ProductCategoryUpdateRequest request, ProductCategory category) {
        if (request == null || category == null) {
            return;
        }

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
    }
}

