package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.SupplierDTO;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.SupplierMapper;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierDTO supplierDTO;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setSupplierId(1);
        supplier.setSupplierName("Test Supplier");
        supplier.setContactPhone("0123456789");
        supplier.setAddress("123 Test St");
        supplier.setCreatedAt(LocalDateTime.now());

        supplierDTO = SupplierDTO.builder()
                .supplierId(1)
                .supplierName("Test Supplier")
                .contactPhone("0123456789")
                .address("123 Test St")
                .build();
    }

    @Nested
    @DisplayName("getAllSuppliers")
    class GetAllSuppliers {

        @Test
        @DisplayName("Should return all suppliers")
        void shouldReturnAllSuppliers() {
            Supplier supplier2 = new Supplier();
            supplier2.setSupplierId(2);
            supplier2.setSupplierName("Supplier 2");

            SupplierDTO dto2 = SupplierDTO.builder().supplierId(2).supplierName("Supplier 2").build();

            when(supplierRepository.findAll()).thenReturn(Arrays.asList(supplier, supplier2));
            when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);
            when(supplierMapper.toDTO(supplier2)).thenReturn(dto2);

            List<SupplierDTO> result = supplierService.getAllSuppliers();

            assertThat(result).hasSize(2);
            verify(supplierRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no suppliers exist")
        void shouldReturnEmptyList() {
            when(supplierRepository.findAll()).thenReturn(List.of());

            List<SupplierDTO> result = supplierService.getAllSuppliers();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create supplier successfully")
        void shouldCreateSuccessfully() {
            when(supplierMapper.toEntity(supplierDTO)).thenReturn(supplier);
            when(supplierRepository.save(supplier)).thenReturn(supplier);
            when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

            SupplierDTO result = supplierService.create(supplierDTO, 0, 0);

            assertThat(result.getSupplierName()).isEqualTo("Test Supplier");
            verify(supplierRepository).save(supplier);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Should update supplier successfully")
        void shouldUpdateSuccessfully() {
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(supplierRepository.save(supplier)).thenReturn(supplier);
            when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

            SupplierDTO result = supplierService.update(1, supplierDTO, 0, 0);

            assertThat(result.getSupplierName()).isEqualTo("Test Supplier");
            verify(supplierMapper).updateEntity(supplierDTO, supplier);
            verify(supplierRepository).save(supplier);
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void shouldThrowWhenNotFound() {
            when(supplierRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> supplierService.update(999, supplierDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(SupplierErrorCode.SUPPLIER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return supplier by ID")
        void shouldReturnById() {
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

            SupplierDTO result = supplierService.getById(1, 0, 0);

            assertThat(result.getSupplierId()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void shouldThrowWhenNotFound() {
            when(supplierRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> supplierService.getById(999, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(SupplierErrorCode.SUPPLIER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should delete supplier successfully")
        void shouldDeleteSuccessfully() {
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));

            supplierService.delete(1, 0, 0);

            verify(supplierRepository).delete(supplier);
        }

        @Test
        @DisplayName("Should throw exception when supplier not found")
        void shouldThrowWhenNotFound() {
            when(supplierRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> supplierService.delete(999, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(SupplierErrorCode.SUPPLIER_NOT_FOUND);
        }
    }
}
