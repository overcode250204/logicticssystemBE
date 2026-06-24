package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUnitCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ProductUnitUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ProductUnitResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.UnitResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.ProductUnit;
import com.overcode250204.smartlogicticssystem.entities.Unit;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductUnitRepository;
import com.overcode250204.smartlogicticssystem.repositories.UnitRepository;
import com.overcode250204.smartlogicticssystem.services.IProductUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductUnitServiceImpl extends BaseServiceImpl implements IProductUnitService {

    private static final int ADMIN_ROLE_ID = 1;

    private final ProductUnitRepository productUnitRepository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;

    @Override
    @Transactional
    public ProductUnitResponseDTO create(ProductUnitCreateRequest request, int roleId, int userId) {
        requireAdmin(roleId);

        Product product = findByIdOrThrow(productRepository, request.getProductId(),
                ProductErrorCode.PRODUCT_NOT_FOUND);

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // Prevent duplicate product-unit mapping
        if (productUnitRepository.existsByProduct_ProductIdAndUnit_Id(product.getProductId(), unit.getId())) {
            throw new AppException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        ProductUnit productUnit = new ProductUnit();
        productUnit.setProduct(product);
        productUnit.setUnit(unit);
        productUnit.setConversionFactor(request.getConversionFactor());

        return toResponse(productUnitRepository.save(productUnit));
    }

    @Override
    @Transactional
    public ProductUnitResponseDTO update(Long id, ProductUnitUpdateRequest request, int roleId, int userId) {
        requireAdmin(roleId);

        ProductUnit productUnit = findByIdOrThrow(productUnitRepository, id,
                ProductErrorCode.PRODUCT_NOT_FOUND);

        productUnit.setConversionFactor(request.getConversionFactor());

        return toResponse(productUnitRepository.save(productUnit));
    }

    @Override
    public ProductUnitResponseDTO getById(Long id, int roleId, int userId) {
        requireAdmin(roleId);
        ProductUnit productUnit = findByIdOrThrow(productUnitRepository, id,
                ProductErrorCode.PRODUCT_NOT_FOUND);
        return toResponse(productUnit);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        requireAdmin(roleId);
        ProductUnit productUnit = findByIdOrThrow(productUnitRepository, id,
                ProductErrorCode.PRODUCT_NOT_FOUND);
        productUnitRepository.delete(productUnit);
    }

    @Override
    public List<ProductUnitResponseDTO> getByProductId(Long productId, int roleId, int userId) {
        requireAdmin(roleId);
        return productUnitRepository.findAllByProduct_ProductId(productId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void requireAdmin(int roleId) {
        if (roleId != ADMIN_ROLE_ID) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    private ProductUnitResponseDTO toResponse(ProductUnit pu) {
        UnitResponseDTO unitDTO = null;
        if (pu.getUnit() != null) {
            unitDTO = UnitResponseDTO.builder()
                    .id(pu.getUnit().getId())
                    .code(pu.getUnit().getCode())
                    .name(pu.getUnit().getName())
                    .type(pu.getUnit().getType())
                    .build();
        }

        return ProductUnitResponseDTO.builder()
                .id(pu.getId())
                .productId(pu.getProduct() != null ? pu.getProduct().getProductId() : null)
                .productName(pu.getProduct() != null ? pu.getProduct().getProductName() : null)
                .unit(unitDTO)
                .conversionFactor(pu.getConversionFactor())
                .build();
    }
}
