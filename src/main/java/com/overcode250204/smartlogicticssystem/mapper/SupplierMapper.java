package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.dtos.SupplierDTO;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        return SupplierDTO.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .contactPhone(supplier.getContactPhone())
                .address(supplier.getAddress())
                .createdAt(supplier.getCreatedAt())
                .build();
    }

    public InventoryDTO toInventoryDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        return InventoryDTO.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .contactPhone(supplier.getContactPhone())
                .address(supplier.getAddress())
                .createdAt(supplier.getCreatedAt())
                .build();
    }

    public Supplier toEntity(SupplierDTO dto) {
        if (dto == null) {
            return null;
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierId(dto.getSupplierId());
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactPhone(dto.getContactPhone());
        supplier.setAddress(dto.getAddress());
        return supplier;
    }

    public void updateEntity(SupplierDTO dto, Supplier entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setSupplierName(dto.getSupplierName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setAddress(dto.getAddress());
    }
}
