package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.ProductMapper;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import com.overcode250204.smartlogicticssystem.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService extends BaseServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllProductResponses() {
        return productRepository.findAll().stream()
                .map(productMapper::toInventoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getByProductCode(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new AppException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO create(ProductDTO dto, int roleId, int userId) {
        if (productRepository.existsByProductCode(dto.getProductCode())) {
            throw new AppException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Supplier supplier = findByIdOrThrow(supplierRepository, dto.getSupplierId(),
                SupplierErrorCode.SUPPLIER_NOT_FOUND);

        Product product = productMapper.toEntity(dto);
        product.setSupplier(supplier);

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, ProductDTO dto, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, id, ProductErrorCode.PRODUCT_NOT_FOUND);

        // Check if code is changed and if new code already exists
        if (!product.getProductCode().equals(dto.getProductCode()) &&
                productRepository.existsByProductCode(dto.getProductCode())) {
            throw new AppException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Supplier supplier = findByIdOrThrow(supplierRepository, dto.getSupplierId(),
                SupplierErrorCode.SUPPLIER_NOT_FOUND);

        productMapper.updateEntity(dto, product);
        product.setSupplier(supplier);

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO getById(Long id, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, id, ProductErrorCode.PRODUCT_NOT_FOUND);
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, id, ProductErrorCode.PRODUCT_NOT_FOUND);
        productRepository.delete(product);
    }
}
