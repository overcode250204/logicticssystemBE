package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.ProductMapper;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setSupplierId(1);
        supplier.setSupplierName("Test Supplier");

        product = new Product();
        product.setProductId(1L);
        product.setProductCode("PRD-001");
        product.setProductName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setMinStockLevel(10);
        product.setSupplier(supplier);

        productDTO = ProductDTO.builder()
                .productId(1L)
                .productCode("PRD-001")
                .productName("Test Product")
                .price(BigDecimal.valueOf(100))
                .minStockLevel(10)
                .supplierId(1)
                .supplierName("Test Supplier")
                .build();
    }

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("Should return all products")
        void shouldReturnAllProducts() {
            when(productRepository.findAll()).thenReturn(Arrays.asList(product));
            when(productMapper.toDTO(product)).thenReturn(productDTO);

            List<ProductDTO> result = productService.getAllProducts();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductCode()).isEqualTo("PRD-001");
        }

        @Test
        @DisplayName("Should return empty list when no products")
        void shouldReturnEmptyList() {
            when(productRepository.findAll()).thenReturn(List.of());

            List<ProductDTO> result = productService.getAllProducts();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getByProductCode")
    class GetByProductCode {

        @Test
        @DisplayName("Should return product by code")
        void shouldReturnByCode() {
            when(productRepository.findByProductCode("PRD-001")).thenReturn(Optional.of(product));
            when(productMapper.toDTO(product)).thenReturn(productDTO);

            ProductDTO result = productService.getByProductCode("PRD-001");

            assertThat(result.getProductCode()).isEqualTo("PRD-001");
        }

        @Test
        @DisplayName("Should throw when product code not found")
        void shouldThrowWhenCodeNotFound() {
            when(productRepository.findByProductCode("INVALID")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getByProductCode("INVALID"))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateSuccessfully() {
            when(productRepository.existsByProductCode("PRD-001")).thenReturn(false);
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(productMapper.toEntity(productDTO)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toDTO(product)).thenReturn(productDTO);

            ProductDTO result = productService.create(productDTO, 0, 0);

            assertThat(result.getProductCode()).isEqualTo("PRD-001");
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("Should throw when product code already exists")
        void shouldThrowWhenCodeExists() {
            when(productRepository.existsByProductCode("PRD-001")).thenReturn(true);

            assertThatThrownBy(() -> productService.create(productDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_ALREADY_EXISTS);

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when supplier not found")
        void shouldThrowWhenSupplierNotFound() {
            when(productRepository.existsByProductCode("PRD-001")).thenReturn(false);
            when(supplierRepository.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.create(productDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(SupplierErrorCode.SUPPLIER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Should update product successfully with same code")
        void shouldUpdateWithSameCode() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toDTO(product)).thenReturn(productDTO);

            ProductDTO result = productService.update(1L, productDTO, 0, 0);

            assertThat(result.getProductCode()).isEqualTo("PRD-001");
            verify(productMapper).updateEntity(productDTO, product);
        }

        @Test
        @DisplayName("Should update product successfully with new unique code")
        void shouldUpdateWithNewUniqueCode() {
            ProductDTO updatedDTO = ProductDTO.builder()
                    .productCode("PRD-002")
                    .productName("Updated Product")
                    .price(BigDecimal.valueOf(200))
                    .supplierId(1)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.existsByProductCode("PRD-002")).thenReturn(false);
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toDTO(product)).thenReturn(updatedDTO);

            ProductDTO result = productService.update(1L, updatedDTO, 0, 0);

            assertThat(result.getProductCode()).isEqualTo("PRD-002");
        }

        @Test
        @DisplayName("Should throw when changing to existing product code")
        void shouldThrowWhenChangingToExistingCode() {
            ProductDTO updatedDTO = ProductDTO.builder()
                    .productCode("PRD-EXISTING")
                    .supplierId(1)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.existsByProductCode("PRD-EXISTING")).thenReturn(true);

            assertThatThrownBy(() -> productService.update(1L, updatedDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        @Test
        @DisplayName("Should throw when product not found")
        void shouldThrowWhenProductNotFound() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.update(999L, productDTO, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return product by ID")
        void shouldReturnById() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productMapper.toDTO(product)).thenReturn(productDTO);

            ProductDTO result = productService.getById(1L, 0, 0);

            assertThat(result.getProductId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw when product not found")
        void shouldThrowWhenNotFound() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getById(999L, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteSuccessfully() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.delete(1L, 0, 0);

            verify(productRepository).delete(product);
        }

        @Test
        @DisplayName("Should throw when product not found")
        void shouldThrowWhenNotFound() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.delete(999L, 0, 0))
                    .isInstanceOf(AppException.class)
                    .extracting(e -> ((AppException) e).getErrorCode())
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }
}
