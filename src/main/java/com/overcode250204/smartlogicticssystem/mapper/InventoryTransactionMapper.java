package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.InventoryTransaction;
import com.overcode250204.smartlogicticssystem.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class InventoryTransactionMapper {

    public InventoryTransactionDTO toDTO(InventoryTransaction entity) {
        if (entity == null) {
            return null;
        }

        InventoryBatch batch = entity.getBatch();
        InventoryTransactionDTO dto = new InventoryTransactionDTO();
        dto.setTransactionId(entity.getTransactionId());
        dto.setBatchId(batch != null ? batch.getBatchId() : null);
        dto.setType(entity.getType());
        dto.setQuantity(entity.getQuantity());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public InventoryDTO toInventoryDTO(InventoryTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        InventoryBatch batch = transaction.getBatch();
        Product product = batch != null ? batch.getProduct() : null;
        return InventoryDTO.builder()
                .transactionId(transaction.getTransactionId())
                .batchId(batch != null ? batch.getBatchId() : null)
                .productId(product != null ? product.getProductId() : null)
                .productName(product != null ? product.getProductName() : null)
                .type(transaction.getType() != null ? transaction.getType().name() : null)
                .quantity(transaction.getQuantity())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public InventoryTransaction toEntity(InventoryTransactionDTO dto) {
        if (dto == null) {
            return null;
        }

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setType(dto.getType());
        transaction.setQuantity(dto.getQuantity());
        transaction.setCreatedAt(dto.getCreatedAt());
        return transaction;
    }
}
