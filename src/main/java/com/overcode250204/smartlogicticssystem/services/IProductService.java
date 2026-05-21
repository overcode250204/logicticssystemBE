package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;

import java.util.List;

public interface IProductService extends BaseService<ProductDTO, Long> {
    List<ProductDTO> getAllProducts();

    ProductDTO getByProductCode(String productCode);
}
