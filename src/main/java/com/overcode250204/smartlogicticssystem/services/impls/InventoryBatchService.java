package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InventoryBatchMapper;
import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import com.overcode250204.smartlogicticssystem.exception.InventoryErrorCode;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
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
    private final IInventoryTransactionService transactionService;

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

    @Override
    @Transactional
    public InventoryBatchDTO create(InventoryBatchDTO dto, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, dto.getProductId(), ProductErrorCode.PRODUCT_NOT_FOUND);

        InventoryBatch batch = batchMapper.toEntity(dto);
        batch.setProduct(product);
        batch.setRemainingQuantity(dto.getQuantity());

        InventoryBatchDTO savedBatch = batchMapper.toDTO(batchRepository.save(batch));

        // Log transaction
        InventoryTransactionDTO transactionDTO = new InventoryTransactionDTO();
        transactionDTO.setBatchId(savedBatch.getBatchId());
        transactionDTO.setQuantity(savedBatch.getQuantity());
        transactionDTO.setType(InventoryTransactionType.IMPORT);

        transactionService.create(transactionDTO, roleId, userId);

        return savedBatch;
    }

    @Override
    @Transactional
    public InventoryBatchDTO update(Long id, InventoryBatchDTO dto, int roleId, int userId) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, id, InventoryErrorCode.BATCH_NOT_FOUND);
        Product product = findByIdOrThrow(productRepository, dto.getProductId(), ProductErrorCode.PRODUCT_NOT_FOUND);

        batch.setProduct(product);
        batch.setQuantity(dto.getQuantity());
        batch.setRemainingQuantity(dto.getRemainingQuantity());
        batch.setExpirationDate(dto.getExpirationDate());
        batch.setStatus(dto.getStatus());

        return batchMapper.toDTO(batchRepository.save(batch));
    }

    @Override
    public InventoryBatchDTO getById(Long id, int roleId, int userId) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, id, InventoryErrorCode.BATCH_NOT_FOUND);
        return batchMapper.toDTO(batch);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, id, InventoryErrorCode.BATCH_NOT_FOUND);
        batchRepository.delete(batch);
    }
}