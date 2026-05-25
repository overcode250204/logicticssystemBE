package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryBatchResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryExportResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryBatchSimpleResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryBatchMapper {

    private final ProductMapper productMapper;
    private final SupplierMapper supplierMapper;

    public InventoryBatchResponseDTO toResponse(InventoryBatch batch) {
        if (batch == null) {
            return null;
        }

        Product product = batch.getProduct();
        Supplier supplier = product != null ? product.getSupplier() : null;
        return InventoryBatchResponseDTO.builder()
                .batchId(batch.getBatchId())
                .product(productMapper.toSimpleResponse(product))
                .supplier(supplierMapper.toSimpleResponse(supplier))
                .importDate(batch.getImportDate())
                .expirationDate(batch.getExpirationDate())
                .quantity(batch.getQuantity())
                .remainingQuantity(batch.getRemainingQuantity())
                .status(batch.getStatus())
                .build();
    }

    public InventoryBatchSimpleResponseDTO toSimpleResponse(InventoryBatch batch) {
        if (batch == null) {
            return null;
        }

        return InventoryBatchSimpleResponseDTO.builder()
                .batchId(batch.getBatchId())
                .importDate(batch.getImportDate())
                .expirationDate(batch.getExpirationDate())
                .quantity(batch.getQuantity())
                .remainingQuantity(batch.getRemainingQuantity())
                .status(batch.getStatus())
                .build();
    }

    public InventoryExportResponseDTO toExportResponse(Product product, int requestedQuantity, int exportedQuantity,
            int remainingStock) {
        return InventoryExportResponseDTO.builder()
                .product(productMapper.toSimpleResponse(product))
                .requestedQuantity(requestedQuantity)
                .exportedQuantity(exportedQuantity)
                .remainingStock(remainingStock)
                .build();
    }

    public InventoryBatch toEntity(InventoryBatchCreateRequest request) {
        if (request == null) {
            return null;
        }

        InventoryBatch batch = new InventoryBatch();
        batch.setImportDate(request.getImportDate());
        batch.setExpirationDate(request.getExpirationDate());
        batch.setQuantity(request.getQuantity());
        batch.setRemainingQuantity(request.getQuantity());
        batch.setStatus(request.getStatus());
        return batch;
    }

    public void updateEntity(InventoryBatchUpdateRequest request, InventoryBatch batch) {
        if (request == null || batch == null) {
            return;
        }

        batch.setQuantity(request.getQuantity());
        batch.setRemainingQuantity(request.getRemainingQuantity());
        batch.setExpirationDate(request.getExpirationDate());
        batch.setStatus(request.getStatus());
    }
}
