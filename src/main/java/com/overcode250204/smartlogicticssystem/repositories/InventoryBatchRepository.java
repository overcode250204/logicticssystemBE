package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {
    @Query("""
    SELECT COALESCE(SUM(b.remainingQuantity), 0)
    FROM InventoryBatch b
    WHERE b.product.productId = :productId
""")
    Integer sumRemainingQuantityByProductId(Long productId);
    List<InventoryBatch> findByProduct_ProductIdAndRemainingQuantityGreaterThan(
            Long productId,
            Integer remainingQuantity,
            Sort sort
    );


    List<InventoryBatch> findByProductProductNameContainingIgnoreCase(String productName);
    List<InventoryBatch> findByProductSupplierSupplierNameContainingIgnoreCase(String supplierName);

    long countByStatus(String status);
}
