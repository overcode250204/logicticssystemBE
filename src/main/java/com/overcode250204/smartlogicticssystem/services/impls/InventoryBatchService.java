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
import com.overcode250204.smartlogicticssystem.enums.InventoryBatchStatus;
import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.InventoryErrorCode;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InventoryBatchMapper;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import com.overcode250204.smartlogicticssystem.utils.BarcodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryBatchService extends BaseServiceImpl implements IInventoryBatchService {

    private static final String BARCODE_FOLDER = "barcodes";
    private static final String PNG_CONTENT_TYPE = "image/png";

    private final InventoryBatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final InventoryBatchMapper batchMapper;
    private final IInventoryTransactionService transactionService;
    private final S3FileService s3FileService;

    @Override
    @Transactional
    public InventoryExportResponseDTO exportStock(InventoryExportRequest request) {
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
        List<InventoryExportBatchDTO> exportedBatches = new ArrayList<>();
        for (InventoryBatch batch : batches) {
            if (remainingToExport <= 0) {
                break;
            }

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
    public List<InventoryBatchResponseDTO> getBatchesByProductId(Long productId) {
        List<InventoryBatch> batches = batchRepository.findByProduct_ProductIdOrderByExpirationDateAsc(productId);
        return batches.stream().map(batch -> {
            InventoryBatchResponseDTO dto = batchMapper.toResponse(batch);
            dto.setStatus(resolveBatchStatus(batch));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryBatchResponseDTO create(InventoryBatchCreateRequest request, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, request.getProductId(), ProductErrorCode.PRODUCT_NOT_FOUND);

        BarcodeGeneratorUtil.GeneratedBarcode generatedBarcode = generateUniqueBarcode();
        String barcodeImageUrl = uploadBarcodeImage(generatedBarcode);

        InventoryBatch batch = batchMapper.toEntity(request);
        batch.setProduct(product);
        batch.setReceived(request.isReceived());
        batch.setReceivedAt(LocalDateTime.now());
        batch.setBarcode(generatedBarcode.barcode());
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
    public InventoryBatchBarcodeResponseDTO getBatchByBarcode(String barcode) {
        InventoryBatch batch = batchRepository.findByBarcode(barcode)
                .orElseThrow(() -> new AppException(InventoryErrorCode.BARCODE_NOT_FOUND));
        return batchMapper.toBarcodeResponse(batch);
    }

    @Override
    @Transactional
    public InventoryBatchBarcodeResponseDTO deductBatchQuantity(Long batchId, Integer quantity) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, batchId, InventoryErrorCode.BATCH_NOT_FOUND);

        if (quantity == null || quantity <= 0) {
            throw new AppException(ProductErrorCode.INVALID_QUANTITY);
        }

        int remainingQuantity = batch.getRemainingQuantity() != null ? batch.getRemainingQuantity() : 0;
        if (quantity > remainingQuantity) {
            throw new AppException(InventoryErrorCode.INSUFFICIENT_STOCK);
        }

        batch.setRemainingQuantity(remainingQuantity - quantity);
        InventoryBatch savedBatch = batchRepository.save(batch);

        try {
            InventoryTransactionCreateRequest transactionRequest = new InventoryTransactionCreateRequest();
            transactionRequest.setBatchId(savedBatch.getBatchId());
            transactionRequest.setType(InventoryTransactionType.EXPORT);
            transactionRequest.setQuantity(quantity);
            transactionService.create(transactionRequest, 0, 0);
        } catch (Exception e) {
            throw new AppException(InventoryErrorCode.TRANSACTION_RECORD_FAILED);
        }

        return batchMapper.toBarcodeResponse(savedBatch);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        InventoryBatch batch = findByIdOrThrow(batchRepository, id, InventoryErrorCode.BATCH_NOT_FOUND);
        batchRepository.delete(batch);
    }

    private InventoryBatchStatus resolveBatchStatus(InventoryBatch batch) {
        if (batch.getRemainingQuantity() != null && batch.getRemainingQuantity() <= 0) {
            return InventoryBatchStatus.OUT_OF_STOCK;
        }
        if (batch.getExpirationDate() != null
                && batch.getExpirationDate().isBefore(java.time.LocalDateTime.now().plusDays(30))) {
            return InventoryBatchStatus.EXPIRING_SOON;
        }
        if (batch.getRemainingQuantity() != null && batch.getRemainingQuantity() <= 10) {
            return InventoryBatchStatus.LOW_STOCK;
        }
        return InventoryBatchStatus.NORMAL;
    }

    private BarcodeGeneratorUtil.GeneratedBarcode generateUniqueBarcode() {
        BarcodeGeneratorUtil.GeneratedBarcode generatedBarcode;

        do {
            generatedBarcode = BarcodeGeneratorUtil.generateEAN13Barcode(generateBarcodeSeed());
        } while (batchRepository.existsByBarcode(generatedBarcode.barcode()));

        return generatedBarcode;
    }

    private String generateBarcodeSeed() {
        long seed = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        return Long.toString(seed);
    }

    private String uploadBarcodeImage(BarcodeGeneratorUtil.GeneratedBarcode generatedBarcode) {
        String key = "%s/%s.png".formatted(BARCODE_FOLDER, generatedBarcode.barcode());
        return s3FileService.uploadBytes(generatedBarcode.pngBytes(), key, PNG_CONTENT_TYPE);
    }
}
