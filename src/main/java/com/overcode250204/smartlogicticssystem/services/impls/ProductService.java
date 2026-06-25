package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.ProductCategory;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.entities.Unit;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.CategoryErrorCode;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.exception.StorageErrorCode;
import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UnitErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.ProductMapper;
import com.overcode250204.smartlogicticssystem.repositories.ProductCategoryRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import com.overcode250204.smartlogicticssystem.repositories.UnitRepository;
import com.overcode250204.smartlogicticssystem.services.IProductService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService extends BaseServiceImpl implements IProductService {

    private static final String PRODUCT_IMAGE_FOLDER = "products";
    private static final long MAX_PRODUCT_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_PRODUCT_IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );
    private static final Set<String> ALLOWED_PRODUCT_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final UnitRepository unitRepository;
    private final ProductMapper productMapper;
    private final S3FileService s3FileService;

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
        return create(request, null, roleId, userId);
    }

    @Override
    @Transactional
    public ProductResponseDTO create(ProductCreateRequest request, MultipartFile image, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, request.getSupplierId(),
                SupplierErrorCode.SUPPLIER_NOT_FOUND);

        ProductCategory category = findByIdOrThrow(productCategoryRepository, request.getCategoryId(),
                CategoryErrorCode.CATEGORY_NOT_FOUND);

        Product product = productMapper.toEntity(request);
        product.setProductCode(UUID.randomUUID().toString());
        product.setSupplier(supplier);
        product.setCategory(category);

        // Resolve base unit if provided
        if (request.getBaseUnitId() != null) {
            Unit baseUnit = unitRepository.findById(request.getBaseUnitId())
                    .orElseThrow(() -> new AppException(UnitErrorCode.UNIT_NOT_FOUND));
            product.setBaseUnit(baseUnit);
        }

        validateProductImage(image);

        String uploadedImageUrl = null;
        try {
            if (image != null && !image.isEmpty()) {
                uploadedImageUrl = s3FileService.uploadFile(image, PRODUCT_IMAGE_FOLDER);
                product.setImageUrl(uploadedImageUrl);
            }

            Product savedProduct = productRepository.save(product);
            productRepository.flush();
            return productMapper.toResponse(savedProduct);
        } catch (RuntimeException e) {
            deleteUploadedImageAfterFailure(uploadedImageUrl);
            throw e;
        }
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductUpdateRequest request, int roleId, int userId) {
        return update(id, request, null, false, roleId, userId);
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductUpdateRequest request, MultipartFile image, boolean removeImage,
            int roleId, int userId) {
        Product product = findByIdOrThrow(productRepository, id, ProductErrorCode.PRODUCT_NOT_FOUND);
        String oldImageUrl = product.getImageUrl();

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

        // Resolve base unit if provided
        if (request.getBaseUnitId() != null) {
            Unit baseUnit = unitRepository.findById(request.getBaseUnitId())
                    .orElseThrow(() -> new AppException(UnitErrorCode.UNIT_NOT_FOUND));
            product.setBaseUnit(baseUnit);
        }

        validateProductImage(image);

        String uploadedImageUrl = null;
        try {
            if (image != null && !image.isEmpty()) {
                uploadedImageUrl = s3FileService.uploadFile(image, PRODUCT_IMAGE_FOLDER);
                product.setImageUrl(uploadedImageUrl);
            } else if (removeImage) {
                product.setImageUrl(null);
            }

            Product savedProduct = productRepository.save(product);
            productRepository.flush();
            ProductResponseDTO response = productMapper.toResponse(savedProduct);
            scheduleImageDeleteAfterCommit(oldImageUrl, product.getImageUrl());
            return response;
        } catch (RuntimeException e) {
            deleteUploadedImageAfterFailure(uploadedImageUrl);
            throw e;
        }
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
        String oldImageUrl = product.getImageUrl();
        productRepository.delete(product);
        productRepository.flush();
        scheduleImageDeleteAfterCommit(oldImageUrl);
    }

    private void validateProductImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return;
        }

        if (image.getSize() > MAX_PRODUCT_IMAGE_SIZE) {
            throw new AppException(StorageErrorCode.FILE_TOO_LARGE);
        }

        String contentType = image.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_PRODUCT_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(StorageErrorCode.INVALID_IMAGE_TYPE);
        }

        String originalFilename = image.getOriginalFilename();
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!ALLOWED_PRODUCT_IMAGE_EXTENSIONS.contains(extension)) {
                throw new AppException(StorageErrorCode.INVALID_IMAGE_TYPE);
            }
        }
    }

    private void deleteUploadedImageAfterFailure(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return;
        }

        try {
            s3FileService.deleteFileByPublicUrl(imageUrl);
        } catch (RuntimeException cleanupException) {
            log.warn("Failed to delete uploaded product image after rollback. imageUrl={}", imageUrl, cleanupException);
        }
    }

    private void scheduleImageDeleteAfterCommit(String oldImageUrl, String currentImageUrl) {
        if (!StringUtils.hasText(oldImageUrl) || oldImageUrl.equals(currentImageUrl)) {
            return;
        }

        scheduleImageDeleteAfterCommit(oldImageUrl);
    }

    private void scheduleImageDeleteAfterCommit(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    deleteImageQuietly(imageUrl);
                }
            });
            return;
        }

        deleteImageQuietly(imageUrl);
    }

    private void deleteImageQuietly(String imageUrl) {
        try {
            s3FileService.deleteFileByPublicUrl(imageUrl);
        } catch (RuntimeException e) {
            log.warn("Failed to delete product image from S3. imageUrl={}", imageUrl, e);
        }
    }
}
