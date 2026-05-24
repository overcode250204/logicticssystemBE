package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;

import java.util.List;

public interface IProductService extends BaseService<ProductDTO, Long> {
    List<ProductDTO> getAllProducts();

    List<InventoryDTO> getAllProductResponses();

    ProductDTO getByProductCode(String productCode);
}
