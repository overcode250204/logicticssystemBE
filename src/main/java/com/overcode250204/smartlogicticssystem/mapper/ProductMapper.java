package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        Supplier supplier = product.getSupplier();
        return ProductDTO.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .price(product.getPrice())
                .minStockLevel(product.getMinStockLevel())
                .supplierId(supplier != null ? supplier.getSupplierId() : null)
                .supplierName(supplier != null ? supplier.getSupplierName() : null)
                .build();
    }

    public InventoryDTO toInventoryDTO(Product product) {
        return toProductInventoryDTO(product);
    }

    public InventoryDTO toProductSummaryResponse(Product product) {
        return toProductInventoryDTO(product);
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setProductCode(dto.getProductCode());
        product.setProductName(dto.getProductName());
        product.setMinStockLevel(dto.getMinStockLevel());
        product.setPrice(dto.getPrice());
        return product;
    }

    public void updateEntity(ProductDTO dto, Product entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setProductCode(dto.getProductCode());
        entity.setProductName(dto.getProductName());
        entity.setMinStockLevel(dto.getMinStockLevel());
        entity.setPrice(dto.getPrice());
    }

    private InventoryDTO toProductInventoryDTO(Product product) {
        if (product == null) {
            return null;
        }

        Supplier supplier = product.getSupplier();
        return InventoryDTO.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .minStockLevel(product.getMinStockLevel())
                .price(product.getPrice())
                .supplierId(supplier != null ? supplier.getSupplierId() : null)
                .supplierName(supplier != null ? supplier.getSupplierName() : null)
                .build();
    }
}
