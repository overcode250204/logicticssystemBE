package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryBatchMapper {
    @Mapping(target = "productId", source = "product.productId")
    InventoryBatchDTO toDTO(InventoryBatch entity);

    @Mapping(target = "product", ignore = true)
    InventoryBatch toEntity(InventoryBatchDTO dto);
}
