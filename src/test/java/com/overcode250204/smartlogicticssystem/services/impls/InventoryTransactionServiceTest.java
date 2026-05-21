package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.InventoryTransaction;
import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.InventoryErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.InventoryTransactionMapper;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.InventoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryTransactionServiceTest {

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @Mock
    private InventoryBatchRepository batchRepository;

    @Mock
    private InventoryTransactionMapper transactionMapper;

    @InjectMocks
    private InventoryTransactionService transactionService;

    private InventoryBatch batch;
    private InventoryTransaction transaction;
    private InventoryTransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        batch = new InventoryBatch();
        batch.setBatchId(1L);
        batch.setQuantity(100);
        batch.setRemainingQuantity(100);

        transaction = new InventoryTransaction();
        transaction.setTransactionId(1L);
        transaction.setBatch(batch);
        transaction.setType(InventoryTransactionType.IMPORT);
        transaction.setQuantity(100);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionDTO = new InventoryTransactionDTO();
        transactionDTO.setTransactionId(1L);
        transactionDTO.setBatchId(1L);
        transactionDTO.setType(InventoryTransactionType.IMPORT);
        transactionDTO.setQuantity(100);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create transaction successfully")
        void shouldCreateSuccessfully() {
            when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
            when(transactionRepository.save(transaction)).thenReturn(transaction);
            when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

            InventoryTransactionDTO result = transactionService.create(transactionDTO, 0, 0);

            assertThat(result.getTransactionId()).isEqualTo(1L);
            assertThat(result.getType()).isEqualTo(InventoryTransactionType.IMPORT);
            verify(transactionRepository).save(transaction);
        }

        @Test
        @DisplayName("Should throw when batch not found")
        void shouldThrowWhenBatchNotFound() {
            when(transactionMapper.toEntity(transactionDTO)).thenReturn(transaction);
            when(batchRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.create(transactionDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.BATCH_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Should update transaction successfully")
        void shouldUpdateSuccessfully() {
            InventoryTransactionDTO updateDTO = new InventoryTransactionDTO();
            updateDTO.setBatchId(1L);
            updateDTO.setType(InventoryTransactionType.EXPORT);
            updateDTO.setQuantity(50);

            when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
            when(transactionRepository.save(transaction)).thenReturn(transaction);
            when(transactionMapper.toDTO(transaction)).thenReturn(updateDTO);

            InventoryTransactionDTO result = transactionService.update(1L, updateDTO, 0, 0);

            assertThat(result.getType()).isEqualTo(InventoryTransactionType.EXPORT);
            verify(transactionRepository).save(transaction);
        }

        @Test
        @DisplayName("Should throw when transaction not found")
        void shouldThrowWhenTransactionNotFound() {
            when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.update(999L, transactionDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.TRANSACTION_NOT_FOUND);
        }

        @Test
        @DisplayName("Should throw when batch not found during update")
        void shouldThrowWhenBatchNotFoundDuringUpdate() {
            when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
            when(batchRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.update(1L, transactionDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.BATCH_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return transaction by ID")
        void shouldReturnById() {
            when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
            when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

            InventoryTransactionDTO result = transactionService.getById(1L, 0, 0);

            assertThat(result.getTransactionId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw when transaction not found")
        void shouldThrowWhenNotFound() {
            when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.getById(999L, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.TRANSACTION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should delete transaction successfully")
        void shouldDeleteSuccessfully() {
            when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

            transactionService.delete(1L, 0, 0);

            verify(transactionRepository).delete(transaction);
        }

        @Test
        @DisplayName("Should throw when transaction not found")
        void shouldThrowWhenNotFound() {
            when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.delete(999L, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(InventoryErrorCode.TRANSACTION_NOT_FOUND);
        }
    }
}
