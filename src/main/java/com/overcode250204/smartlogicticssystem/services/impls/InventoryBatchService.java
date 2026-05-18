package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.exception.InventoryBatchErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InventoryBatchMapper;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryBatchService extends BaseServiceImpl implements IInventoryBatchService {

    private final InventoryBatchRepository inventoryBatchRepository;
    private final ProductRepository productRepository;
    private final InventoryBatchMapper inventoryBatchMapper;

    @Override
    public List<InventoryBatchDTO> getAll() {
        return inventoryBatchRepository.findAll().stream()
                .map(inventoryBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryBatchDTO create(InventoryBatchDTO dto) {
        Product product = findByIdOrThrow(productRepository, dto.getProductId(), InventoryBatchErrorCode.PRODUCT_NOT_FOUND);
        InventoryBatch batch = inventoryBatchMapper.toEntity(dto);
        batch.setProduct(product);
        return inventoryBatchMapper.toDTO(inventoryBatchRepository.save(batch));
    }

    @Override
    public InventoryBatchDTO update(Long id, InventoryBatchDTO dto) {
        InventoryBatch batch = findByIdOrThrow(inventoryBatchRepository, id, InventoryBatchErrorCode.BATCH_NOT_FOUND);
        if (dto.getProductId() != null) {
            Product product = findByIdOrThrow(productRepository, dto.getProductId(), InventoryBatchErrorCode.PRODUCT_NOT_FOUND);
            batch.setProduct(product);
        }
        batch.setExpirationDate(dto.getExpirationDate());
        batch.setQuantity(dto.getQuantity());
        batch.setStatus(dto.getStatus());
        if (dto.getImportDate() != null) {
            batch.setImportDate(dto.getImportDate());
        }
        return inventoryBatchMapper.toDTO(inventoryBatchRepository.save(batch));
    }

    @Override
    public InventoryBatchDTO getById(Long id) {
        InventoryBatch batch = findByIdOrThrow(inventoryBatchRepository, id, InventoryBatchErrorCode.BATCH_NOT_FOUND);
        return inventoryBatchMapper.toDTO(batch);
    }

    @Override
    public void delete(Long id) {
        InventoryBatch batch = findByIdOrThrow(inventoryBatchRepository, id, InventoryBatchErrorCode.BATCH_NOT_FOUND);
        inventoryBatchRepository.delete(batch);
    }
}
