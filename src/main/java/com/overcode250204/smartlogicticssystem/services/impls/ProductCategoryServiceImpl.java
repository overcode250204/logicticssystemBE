package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductCategoryUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductCategoryResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.ProductCategory;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.CategoryErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.ProductCategoryMapper;
import com.overcode250204.smartlogicticssystem.repositories.ProductCategoryRepository;
import com.overcode250204.smartlogicticssystem.services.IProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends BaseServiceImpl implements IProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @Override
    public List<ProductCategoryResponseDTO> getAllCategories() {
        return productCategoryRepository.findAll().stream()
                .map(productCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryResponseDTO getByCategoryCode(String categoryCode) {
        ProductCategory category = productCategoryRepository.findByCategoryCode(categoryCode);
        if (category == null) {
            throw new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
        return productCategoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public ProductCategoryResponseDTO create(ProductCategoryCreateRequest request, int roleId, int userId) {
        ProductCategory category = productCategoryMapper.toEntity(request);
        category.setCategoryCode(UUID.randomUUID().toString());
        ProductCategory savedCategory = productCategoryRepository.save(category);

        return productCategoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public ProductCategoryResponseDTO update(Long id, ProductCategoryUpdateRequest request, int roleId, int userId) {
        ProductCategory category = findByIdOrThrow(productCategoryRepository, id, CategoryErrorCode.CATEGORY_NOT_FOUND);


        productCategoryMapper.updateEntity(request, category);
        ProductCategory updatedCategory = productCategoryRepository.save(category);

        return productCategoryMapper.toResponse(updatedCategory);
    }

    @Override
    public ProductCategoryResponseDTO getById(Long id, int roleId, int userId) {
        ProductCategory category = findByIdOrThrow(productCategoryRepository, id, CategoryErrorCode.CATEGORY_NOT_FOUND);
        return productCategoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        ProductCategory category = findByIdOrThrow(productCategoryRepository, id, CategoryErrorCode.CATEGORY_NOT_FOUND);
        productCategoryRepository.delete(category);
    }
}

