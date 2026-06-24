package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.ProductUnitCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUnitUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductUnitResponseDTO;

import java.util.List;

public interface IProductUnitService {
    ProductUnitResponseDTO create(ProductUnitCreateRequest request, int roleId, int userId);
    ProductUnitResponseDTO update(Long id, ProductUnitUpdateRequest request, int roleId, int userId);
    ProductUnitResponseDTO getById(Long id, int roleId, int userId);
    void delete(Long id, int roleId, int userId);
    List<ProductUnitResponseDTO> getByProductId(Long productId, int roleId, int userId);
}
