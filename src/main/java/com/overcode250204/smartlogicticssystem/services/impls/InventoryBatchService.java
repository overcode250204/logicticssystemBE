package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InventoryBatchMapper;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryBatchService extends BaseServiceImpl implements IInventoryBatchService {

    private final InventoryBatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final InventoryBatchMapper batchMapper;

    @Override
    @Transactional
    public InventoryBatchDTO createBatch(InventoryBatchDTO dto) {
        Product product = findByIdOrThrow(productRepository, dto.getProductId(), ProductErrorCode.PRODUCT_NOT_FOUND);

        InventoryBatch batch = batchMapper.toEntity(dto);
        batch.setProduct(product);
        batch.setRemainingQuantity(dto.getQuantity());

        return batchMapper.toDTO(batchRepository.save(batch));
    }

    @Override
    public List<InventoryBatchDTO> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(batchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryBatchDTO> getBatchesByProductName(String productName) {
        return batchRepository.findByProductProductNameContainingIgnoreCase(productName).stream()
                .map(batchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryBatchDTO> getBatchesBySupplierName(String supplierName) {
        return batchRepository.findByProductSupplierSupplierNameContainingIgnoreCase(supplierName).stream()
                .map(batchMapper::toDTO)
                .collect(Collectors.toList());
    }
}