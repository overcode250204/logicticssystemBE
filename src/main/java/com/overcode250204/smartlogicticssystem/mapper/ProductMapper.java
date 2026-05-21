package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "supplierId", source = "supplier.supplierId")
    @Mapping(target = "supplierName", source = "supplier.supplierName")
    ProductDTO toDTO(Product product);

    @Mapping(target = "supplier", ignore = true)
    Product toEntity(ProductDTO dto);

    @InheritConfiguration
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    void updateEntity(ProductDTO dto, @MappingTarget Product entity);
}
