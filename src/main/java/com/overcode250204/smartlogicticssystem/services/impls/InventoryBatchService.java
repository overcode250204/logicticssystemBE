package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.exception.AppException;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Transactional
    public InventoryBatchDTO exportStock(InventoryBatchDTO request) {
        Product product = findByIdOrThrow(productRepository, request.getProductId(),
                ProductErrorCode.PRODUCT_NOT_FOUND);

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new AppException(ProductErrorCode.INVALID_QUANTITY);
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "importDate");
        List<InventoryBatch> batches = batchRepository.findByProduct_ProductIdAndRemainingQuantityGreaterThan(
                request.getProductId(), 0, sort);

        int totalAvailable = batches.stream()
                .mapToInt(InventoryBatch::getRemainingQuantity)
                .sum();

        if (totalAvailable < request.getQuantity()) {
            throw new AppException(ProductErrorCode.NOT_ENOUGH_STOCK);
        }

        int remainingToExport = request.getQuantity();
        List<InventoryDTO> affectedBatches = new ArrayList<>();

        for (InventoryBatch batch : batches) {
            if (remainingToExport <= 0)
                break;

            int exportQuantity = Math.min(batch.getRemainingQuantity(), remainingToExport);
            batch.setRemainingQuantity(batch.getRemainingQuantity() - exportQuantity);
            batchRepository.save(batch);

            try{
                InventoryTransactionDTO transactionDTO = new InventoryTransactionDTO();
                transactionDTO.setBatchId(batch.getBatchId());
                transactionDTO.setType(InventoryTransactionType.EXPORT);
                transactionDTO.setQuantity(exportQuantity);
                transactionService.create(transactionDTO, 0, 0);
            } catch (Exception e) {
                throw new AppException(InventoryErrorCode.TRANSACTION_RECORD_FAILED);
            }

            affectedBatches.add(batchMapper.toInventoryDTO(batch));
            remainingToExport -= exportQuantity;
        }

        return InventoryBatchDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .requestedQuantity(request.getQuantity())
                .exportedQuantity(request.getQuantity())
                .remainingQuantity(totalAvailable - request.getQuantity())
                .build();
    }

    @Override
    public List<InventoryBatchDTO> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(batchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllBatchResponses() {
        return batchRepository.findAll().stream()
                .map(batchMapper::toInventoryDTO)
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
