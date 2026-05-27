package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryExportRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryBatchBarcodeResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryBatchResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryExportBatchDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryExportResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InventoryBatchMapper;
import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import com.overcode250204.smartlogicticssystem.exception.InventoryErrorCode;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;

import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
import com.overcode250204.smartlogicticssystem.services.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryBatchService extends BaseServiceImpl implements IInventoryBatchService {

    private final InventoryBatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final InventoryBatchMapper batchMapper;
    private final IInventoryTransactionService transactionService;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public InventoryExportResponseDTO exportStock(InventoryExportRequest request) {
        Product product = findByIdOrThrow(productRepository, request.getProductId(),
                ProductErrorCode.PRODUCT_NOT_FOUND);

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new AppException(ProductErrorCode.INVALID_QUANTITY);
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "expirationDate")
                .and(Sort.by(Sort.Direction.ASC, "importDate"));
        List<InventoryBatch> batches = batchRepository.findByProduct_ProductIdAndRemainingQuantityGreaterThan(
                request.getProductId(), 0, sort);

        int totalAvailable = batches.stream()
                .mapToInt(InventoryBatch::getRemainingQuantity)
                .sum();

        if (totalAvailable < request.getQuantity()) {
            throw new AppException(ProductErrorCode.NOT_ENOUGH_STOCK);
        }

        int remainingToExport = request.getQuantity();
        List<InventoryExportBatchDTO> exportedBatches = new ArrayList<>();
        for (InventoryBatch batch : batches) {
            if (remainingToExport <= 0)
                break;

            int exportQuantity = Math.min(batch.getRemainingQuantity(), remainingToExport);
            batch.setRemainingQuantity(batch.getRemainingQuantity() - exportQuantity);
            batchRepository.save(batch);
            exportedBatches.add(InventoryExportBatchDTO.builder()
                    .batchId(batch.getBatchId())
                    .exportedQuantity(exportQuantity)
                    .remainingQuantity(batch.getRemainingQuantity())
                    .build());

            try {
                InventoryTransactionCreateRequest transactionRequest = new InventoryTransactionCreateRequest();
                transactionRequest.setBatchId(batch.getBatchId());
                transactionRequest.setType(InventoryTransactionType.EXPORT);
                transactionRequest.setQuantity(exportQuantity);
                transactionService.create(transactionRequest, 0, 0);
            } catch (Exception e) {
                throw new AppException(InventoryErrorCode.TRANSACTION_RECORD_FAILED);
            }

            remainingToExport -= exportQuantity;
        }

        return batchMapper.toExportResponse(product, request.getQuantity(), request.getQuantity(),
                totalAvailable - request.getQuantity(), exportedBatches);
    }

    @Override
    public List<InventoryBatchResponseDTO> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryBatchResponseDTO> getBatchesByProductName(String productName) {
        return batchRepository.findByProductProductNameContainingIgnoreCase(productName).stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryBatchResponseDTO> getBatchesBySupplierName(String supplierName) {
        return batchRepository.findByProductSupplierSupplierNameContainingIgnoreCase(supplierName).stream()
                .map(batchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryBatchResponseDTO create(InventoryBatchCreateRequest request, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, request.getProductId(), ProductErrorCode.PRODUCT_NOT_FOUND);

        InventoryBatch batch = batchMapper.toEntity(request);
        batch.setProduct(product);
        String barcode = generateBarcode();
        String barcodeImageUrl = qrCodeService.generateAndUploadQrCode(barcode);
        batch.setBarcode(barcode);
        batch.setBarcodeImageUrl(barcodeImageUrl);
        InventoryBatch savedBatch = batchRepository.save(batch);

        InventoryTransactionCreateRequest transactionRequest = new InventoryTransactionCreateRequest();
        transactionRequest.setBatchId(savedBatch.getBatchId());
        transactionRequest.setQuantity(savedBatch.getQuantity());
        transactionRequest.setType(InventoryTransactionType.IMPORT);

        transactionService.create(transactionRequest, roleId, userId);

        return batchMapper.toResponse(savedBatch);
    }

    @Override
    @Transactional
    public InventoryBatchResponseDTO update(Long id, InventoryBatchUpdateRequest request, int roleId, int userId) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, id, InventoryErrorCode.BATCH_NOT_FOUND);
        Product product = findByIdOrThrow(productRepository, request.getProductId(), ProductErrorCode.PRODUCT_NOT_FOUND);

        batch.setProduct(product);
        batchMapper.updateEntity(request, batch);

        return batchMapper.toResponse(batchRepository.save(batch));
    }

    @Override
    public InventoryBatchResponseDTO getById(Long id, int roleId, int userId) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, id, InventoryErrorCode.BATCH_NOT_FOUND);
        return batchMapper.toResponse(batch);
    }

    @Override
    public InventoryBatchBarcodeResponseDTO getByBarcode(String barcode) {
        InventoryBatch batch = batchRepository.findByBarcode(barcode)
                .orElseThrow(() -> new AppException(InventoryErrorCode.BATCH_NOT_FOUND));
        return batchMapper.toBarcodeResponse(batch);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, id, InventoryErrorCode.BATCH_NOT_FOUND);
        batchRepository.delete(batch);
    }

    private String generateBarcode() {
        String barcode;

        do {
            barcode = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (batchRepository.existsByBarcode(barcode));

        return barcode;
    }
}
