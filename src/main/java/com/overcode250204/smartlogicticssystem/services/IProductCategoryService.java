package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductCategoryResponseDTO;

import java.util.List;

public interface IProductCategoryService {
    ProductCategoryResponseDTO create(ProductCategoryCreateRequest request, int roleId, int userId);

    ProductCategoryResponseDTO update(Long id, ProductCategoryUpdateRequest request, int roleId, int userId);

    ProductCategoryResponseDTO getById(Long id, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    List<ProductCategoryResponseDTO> getAllCategories();

    ProductCategoryResponseDTO getByCategoryCode(String categoryCode);
}

