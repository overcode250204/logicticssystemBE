package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.SupplierDTO;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierDTO toDTO(Supplier supplier);

    @Mapping(target = "createdAt", ignore = true)
    Supplier toEntity(SupplierDTO dto);

    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(SupplierDTO dto, @MappingTarget Supplier entity);
}
