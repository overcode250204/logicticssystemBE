package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.SupplierCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierResponseDTO toResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        return SupplierResponseDTO.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .contactPhone(supplier.getContactPhone())
                .address(supplier.getAddress())
                .createdAt(supplier.getCreatedAt())
                .build();
    }

    public SupplierResponseDTO toSimpleResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        return SupplierResponseDTO.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .build();
    }

    public Supplier toEntity(SupplierCreateRequest request) {
        if (request == null) {
            return null;
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierName(request.getSupplierName());
        supplier.setContactPhone(request.getContactPhone());
        supplier.setAddress(request.getAddress());
        return supplier;
    }

    public void updateEntity(SupplierUpdateRequest request, Supplier entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setSupplierName(request.getSupplierName());
        entity.setContactPhone(request.getContactPhone());
        entity.setAddress(request.getAddress());
    }
}
