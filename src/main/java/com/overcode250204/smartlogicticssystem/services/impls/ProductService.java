package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.ProductCategory;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.CategoryErrorCode;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.ProductMapper;
import com.overcode250204.smartlogicticssystem.repositories.ProductCategoryRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import com.overcode250204.smartlogicticssystem.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService extends BaseServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO getByProductCode(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new AppException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO create(ProductCreateRequest request, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, request.getSupplierId(),
                SupplierErrorCode.SUPPLIER_NOT_FOUND);

        ProductCategory category = findByIdOrThrow(productCategoryRepository, request.getCategoryId(),
                CategoryErrorCode.CATEGORY_NOT_FOUND);

        Product product = productMapper.toEntity(request);
        product.setProductCode(UUID.randomUUID().toString());
        product.setSupplier(supplier);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductUpdateRequest request, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, id, ProductErrorCode.PRODUCT_NOT_FOUND);

        if (!product.getProductCode().equals(request.getProductCode()) &&
                productRepository.existsByProductCode(request.getProductCode())) {
            throw new AppException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Supplier supplier = findByIdOrThrow(supplierRepository, request.getSupplierId(),
                SupplierErrorCode.SUPPLIER_NOT_FOUND);

        ProductCategory category = findByIdOrThrow(productCategoryRepository, request.getCategoryId(),
                CategoryErrorCode.CATEGORY_NOT_FOUND);

        productMapper.updateEntity(request, product);
        product.setSupplier(supplier);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO getById(Long id, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, id, ProductErrorCode.PRODUCT_NOT_FOUND);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, id, ProductErrorCode.PRODUCT_NOT_FOUND);
        productRepository.delete(product);
    }
}
