package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import org.springframework.stereotype.Component;

@Component
public class InventoryBatchMapper {

    public InventoryBatchDTO toDTO(InventoryBatch entity) {
        if (entity == null) {
            return null;
        }

        Product product = entity.getProduct();
        return InventoryBatchDTO.builder()
                .batchId(entity.getBatchId())
                .productId(product != null ? product.getProductId() : null)
                .productName(product != null ? product.getProductName() : null)
                .importDate(entity.getImportDate())
                .expirationDate(entity.getExpirationDate())
                .quantity(entity.getQuantity())
                .remainingQuantity(entity.getRemainingQuantity())
                .status(entity.getStatus())
                .build();
    }

    public InventoryDTO toInventoryDTO(InventoryBatch batch) {
        if (batch == null) {
            return null;
        }

        Product product = batch.getProduct();
        Supplier supplier = product != null ? product.getSupplier() : null;
        return InventoryDTO.builder()
                .batchId(batch.getBatchId())
                .productId(product != null ? product.getProductId() : null)
                .productCode(product != null ? product.getProductCode() : null)
                .productName(product != null ? product.getProductName() : null)
                .supplierId(supplier != null ? supplier.getSupplierId() : null)
                .supplierName(supplier != null ? supplier.getSupplierName() : null)
                .importDate(batch.getImportDate())
                .expirationDate(batch.getExpirationDate())
                .quantity(batch.getQuantity())
                .remainingQuantity(batch.getRemainingQuantity())
                .status(batch.getStatus())
                .build();
    }

    public InventoryBatch toEntity(InventoryBatchDTO dto) {
        if (dto == null) {
            return null;
        }

        InventoryBatch batch = new InventoryBatch();
        batch.setBatchId(dto.getBatchId());
        batch.setImportDate(dto.getImportDate());
        batch.setExpirationDate(dto.getExpirationDate());
        batch.setQuantity(dto.getQuantity());
        batch.setRemainingQuantity(dto.getRemainingQuantity());
        batch.setStatus(dto.getStatus());
        return batch;
    }
}
