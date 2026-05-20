package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {
    @Mapping(target = "batchId", source = "batch.batchId")
    InventoryTransactionDTO toDTO(InventoryTransaction entity);

    @Mapping(target = "batch", ignore = true)
    InventoryTransaction toEntity(InventoryTransactionDTO dto);
}
