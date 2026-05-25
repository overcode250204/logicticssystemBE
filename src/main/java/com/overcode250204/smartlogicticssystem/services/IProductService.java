package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.ProductCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;

import java.util.List;

public interface IProductService {
    ProductResponseDTO create(ProductCreateRequest request, int roleId, int userId);

    ProductResponseDTO update(Long id, ProductUpdateRequest request, int roleId, int userId);

    ProductResponseDTO getById(Long id, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getByProductCode(String productCode);
}
