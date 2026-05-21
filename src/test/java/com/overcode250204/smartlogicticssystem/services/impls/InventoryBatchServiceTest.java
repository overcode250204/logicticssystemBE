package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.dtos.request.ExportStockRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ExportStockResponse;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.InventoryErrorCode;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InventoryBatchMapper;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryBatchServiceTest {

    @Mock
    private InventoryBatchRepository batchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryBatchMapper batchMapper;

    @Mock
    private IInventoryTransactionService transactionService;

    @InjectMocks
    private InventoryBatchService batchService;

    private Product product;
    private InventoryBatch batch1;
    private InventoryBatch batch2;
    private InventoryBatchDTO batchDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");

        batch1 = new InventoryBatch();
        batch1.setBatchId(1L);
        batch1.setProduct(product);
        batch1.setQuantity(100);
        batch1.setRemainingQuantity(80);
        batch1.setImportDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        batch1.setStatus("Good");

        batch2 = new InventoryBatch();
        batch2.setBatchId(2L);
        batch2.setProduct(product);
        batch2.setQuantity(50);
        batch2.setRemainingQuantity(50);
        batch2.setImportDate(LocalDateTime.of(2026, 2, 1, 0, 0));
        batch2.setStatus("Good");

        batchDTO = new InventoryBatchDTO();
        batchDTO.setBatchId(1L);
        batchDTO.setProductId(1L);
        batchDTO.setQuantity(100);
        batchDTO.setRemainingQuantity(80);
        batchDTO.setStatus("Good");
    }

    // ===================== exportStock =====================

    @Nested
    @DisplayName("exportStock")
    class ExportStock {

        @Test
        @DisplayName("Should export from single batch (FIFO)")
        void shouldExportFromSingleBatch() {
            ExportStockRequest request = ExportStockRequest.builder()
                    .productId(1L)
                    .quantity(30)
                    .build();

            InventoryBatchDTO exportedDTO = new InventoryBatchDTO();
            exportedDTO.setBatchId(1L);
            exportedDTO.setRemainingQuantity(50);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepository.findByProduct_ProductIdAndRemainingQuantityGreaterThan(
                    eq(1L), eq(0), any(Sort.class)))
                    .thenReturn(Arrays.asList(batch1, batch2));
            when(batchRepository.save(batch1)).thenReturn(batch1);
            when(batchMapper.toDTO(batch1)).thenReturn(exportedDTO);
            when(transactionService.create(any(InventoryTransactionDTO.class), eq(0), eq(0)))
                    .thenReturn(new InventoryTransactionDTO());

            ExportStockResponse result = batchService.exportStock(request);

            assertThat(result.getProductId()).isEqualTo(1L);
            assertThat(result.getExportedQuantity()).isEqualTo(30);
            assertThat(result.getRemainingStock()).isEqualTo(100); // 130 - 30
            assertThat(result.getBatches()).hasSize(1);
            verify(batchRepository, times(1)).save(any(InventoryBatch.class));
        }

        @Test
        @DisplayName("Should export across multiple batches (FIFO)")
        void shouldExportAcrossMultipleBatches() {
            ExportStockRequest request = ExportStockRequest.builder()
                    .productId(1L)
                    .quantity(100)
                    .build();

            InventoryBatchDTO dto1 = new InventoryBatchDTO();
            dto1.setBatchId(1L);
            InventoryBatchDTO dto2 = new InventoryBatchDTO();
            dto2.setBatchId(2L);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepository.findByProduct_ProductIdAndRemainingQuantityGreaterThan(
                    eq(1L), eq(0), any(Sort.class)))
                    .thenReturn(Arrays.asList(batch1, batch2));
            when(batchRepository.save(any(InventoryBatch.class))).thenAnswer(inv -> inv.getArgument(0));
            when(batchMapper.toDTO(batch1)).thenReturn(dto1);
            when(batchMapper.toDTO(batch2)).thenReturn(dto2);
            when(transactionService.create(any(InventoryTransactionDTO.class), eq(0), eq(0)))
                    .thenReturn(new InventoryTransactionDTO());

            ExportStockResponse result = batchService.exportStock(request);

            assertThat(result.getExportedQuantity()).isEqualTo(100);
            assertThat(result.getBatches()).hasSize(2); // spans 2 batches
            assertThat(result.getRemainingStock()).isEqualTo(30); // 130 - 100
            verify(batchRepository, times(2)).save(any(InventoryBatch.class));
            verify(transactionService, times(2)).create(any(), eq(0), eq(0));
        }

        @Test
        @DisplayName("Should throw when quantity is null")
        void shouldThrowWhenQuantityNull() {
            ExportStockRequest request = ExportStockRequest.builder()
                    .productId(1L)
                    .quantity(null)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> batchService.exportStock(request))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.INVALID_QUANTITY);
        }

        @Test
        @DisplayName("Should throw when quantity is zero")
        void shouldThrowWhenQuantityZero() {
            ExportStockRequest request = ExportStockRequest.builder()
                    .productId(1L)
                    .quantity(0)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> batchService.exportStock(request))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.INVALID_QUANTITY);
        }

        @Test
        @DisplayName("Should throw when quantity is negative")
        void shouldThrowWhenQuantityNegative() {
            ExportStockRequest request = ExportStockRequest.builder()
                    .productId(1L)
                    .quantity(-5)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            assertThatThrownBy(() -> batchService.exportStock(request))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.INVALID_QUANTITY);
        }

        @Test
        @DisplayName("Should throw when not enough stock")
        void shouldThrowWhenNotEnoughStock() {
            ExportStockRequest request = ExportStockRequest.builder()
                    .productId(1L)
                    .quantity(500)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepository.findByProduct_ProductIdAndRemainingQuantityGreaterThan(
                    eq(1L), eq(0), any(Sort.class)))
                    .thenReturn(Arrays.asList(batch1, batch2));

            assertThatThrownBy(() -> batchService.exportStock(request))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.NOT_ENOUGH_STOCK);
        }

        @Test
        @DisplayName("Should throw when product not found")
        void shouldThrowWhenProductNotFound() {
            ExportStockRequest request = ExportStockRequest.builder()
                    .productId(999L)
                    .quantity(10)
                    .build();

            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> batchService.exportStock(request))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    // ===================== getAllBatches =====================

    @Nested
    @DisplayName("getAllBatches")
    class GetAllBatches {

        @Test
        @DisplayName("Should return all batches")
        void shouldReturnAllBatches() {
            when(batchRepository.findAll()).thenReturn(Arrays.asList(batch1, batch2));
            when(batchMapper.toDTO(any(InventoryBatch.class))).thenReturn(batchDTO);

            List<InventoryBatchDTO> result = batchService.getAllBatches();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty list when no batches")
        void shouldReturnEmptyList() {
            when(batchRepository.findAll()).thenReturn(List.of());

            List<InventoryBatchDTO> result = batchService.getAllBatches();

            assertThat(result).isEmpty();
        }
    }

    // ===================== getBatchesByProductName =====================

    @Nested
    @DisplayName("getBatchesByProductName")
    class GetBatchesByProductName {

        @Test
        @DisplayName("Should return batches by product name")
        void shouldReturnByProductName() {
            when(batchRepository.findByProductProductNameContainingIgnoreCase("Test"))
                    .thenReturn(Arrays.asList(batch1));
            when(batchMapper.toDTO(batch1)).thenReturn(batchDTO);

            List<InventoryBatchDTO> result = batchService.getBatchesByProductName("Test");

            assertThat(result).hasSize(1);
        }
    }

    // ===================== getBatchesBySupplierName =====================

    @Nested
    @DisplayName("getBatchesBySupplierName")
    class GetBatchesBySupplierName {

        @Test
        @DisplayName("Should return batches by supplier name")
        void shouldReturnBySupplierName() {
            when(batchRepository.findByProductSupplierSupplierNameContainingIgnoreCase("Supplier"))
                    .thenReturn(Arrays.asList(batch1));
            when(batchMapper.toDTO(batch1)).thenReturn(batchDTO);

            List<InventoryBatchDTO> result = batchService.getBatchesBySupplierName("Supplier");

            assertThat(result).hasSize(1);
        }
    }

    // ===================== create =====================

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create batch and log IMPORT transaction")
        void shouldCreateAndLogTransaction() {
            InventoryBatchDTO inputDTO = new InventoryBatchDTO();
            inputDTO.setProductId(1L);
            inputDTO.setQuantity(100);

            InventoryBatchDTO savedDTO = new InventoryBatchDTO();
            savedDTO.setBatchId(1L);
            savedDTO.setProductId(1L);
            savedDTO.setQuantity(100);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchMapper.toEntity(inputDTO)).thenReturn(batch1);
            when(batchRepository.save(batch1)).thenReturn(batch1);
            when(batchMapper.toDTO(batch1)).thenReturn(savedDTO);
            when(transactionService.create(any(InventoryTransactionDTO.class), eq(0), eq(0)))
                    .thenReturn(new InventoryTransactionDTO());

            InventoryBatchDTO result = batchService.create(inputDTO, 0, 0);

            assertThat(result.getBatchId()).isEqualTo(1L);
            verify(batchRepository).save(batch1);
            verify(transactionService).create(any(InventoryTransactionDTO.class), eq(0), eq(0));
        }

        @Test
        @DisplayName("Should throw when product not found")
        void shouldThrowWhenProductNotFound() {
            InventoryBatchDTO inputDTO = new InventoryBatchDTO();
            inputDTO.setProductId(999L);

            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> batchService.create(inputDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    // ===================== update =====================

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Should update batch successfully")
        void shouldUpdateSuccessfully() {
            InventoryBatchDTO updateDTO = new InventoryBatchDTO();
            updateDTO.setProductId(1L);
            updateDTO.setQuantity(200);
            updateDTO.setRemainingQuantity(150);
            updateDTO.setExpirationDate(LocalDateTime.of(2027, 1, 1, 0, 0));
            updateDTO.setStatus("Good");

            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch1));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchRepository.save(batch1)).thenReturn(batch1);
            when(batchMapper.toDTO(batch1)).thenReturn(updateDTO);

            InventoryBatchDTO result = batchService.update(1L, updateDTO, 0, 0);

            assertThat(result.getQuantity()).isEqualTo(200);
            verify(batchRepository).save(batch1);
        }

        @Test
        @DisplayName("Should throw when batch not found")
        void shouldThrowWhenBatchNotFound() {
            InventoryBatchDTO updateDTO = new InventoryBatchDTO();
            updateDTO.setProductId(1L);

            when(batchRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> batchService.update(999L, updateDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.BATCH_NOT_FOUND);
        }

        @Test
        @DisplayName("Should throw when product not found during update")
        void shouldThrowWhenProductNotFoundDuringUpdate() {
            InventoryBatchDTO updateDTO = new InventoryBatchDTO();
            updateDTO.setProductId(999L);

            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch1));
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> batchService.update(1L, updateDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    // ===================== getById =====================

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return batch by ID")
        void shouldReturnById() {
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch1));
            when(batchMapper.toDTO(batch1)).thenReturn(batchDTO);

            InventoryBatchDTO result = batchService.getById(1L, 0, 0);

            assertThat(result.getBatchId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw when batch not found")
        void shouldThrowWhenNotFound() {
            when(batchRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> batchService.getById(999L, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.BATCH_NOT_FOUND);
        }
    }

    // ===================== delete =====================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should delete batch successfully")
        void shouldDeleteSuccessfully() {
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch1));

            batchService.delete(1L, 0, 0);

            verify(batchRepository).delete(batch1);
        }

        @Test
        @DisplayName("Should throw when batch not found")
        void shouldThrowWhenNotFound() {
            when(batchRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> batchService.delete(999L, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.BATCH_NOT_FOUND);
        }
    }
}
