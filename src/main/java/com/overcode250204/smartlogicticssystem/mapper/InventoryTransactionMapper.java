package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.InventoryTransaction;
import com.overcode250204.smartlogicticssystem.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryTransactionMapper {

    private final InventoryBatchMapper batchMapper;
    private final ProductMapper productMapper;

    public InventoryTransactionResponseDTO toResponse(InventoryTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        InventoryBatch batch = transaction.getBatch();
        Product product = batch != null ? batch.getProduct() : null;
        return InventoryTransactionResponseDTO.builder()
                .transactionId(transaction.getTransactionId())
                .batch(batchMapper.toSimpleResponse(batch))
                .product(productMapper.toSimpleResponse(product))
                .type(transaction.getType())
                .quantity(transaction.getQuantity())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public InventoryTransaction toEntity(InventoryTransactionCreateRequest request) {
        if (request == null) {
            return null;
        }

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setType(request.getType());
        transaction.setQuantity(request.getQuantity());
        return transaction;
    }

    public void updateEntity(InventoryTransactionUpdateRequest request, InventoryTransaction transaction) {
        if (request == null || transaction == null) {
            return;
        }

        transaction.setQuantity(request.getQuantity());
        transaction.setType(request.getType());
    }
}
