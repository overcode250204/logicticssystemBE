package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

    List<InventoryBatch> findByProduct_ProductIdAndRemainingQuantityGreaterThan(
            Long productId,
            Integer remainingQuantity,
            Sort sort
    );

    List<InventoryBatch> findByProduct_ProductIdOrderByExpirationDateAsc(Long productId);

    boolean existsByBarcode(String barcode);

    Optional<InventoryBatch> findByBarcode(String barcode);

    List<InventoryBatch> findByProductProductNameContainingIgnoreCase(String productName);
    List<InventoryBatch> findByProductSupplierSupplierNameContainingIgnoreCase(String supplierName);

    @Query("""
    SELECT b
    FROM InventoryBatch b
    JOIN FETCH b.product p
    WHERE b.remainingQuantity <= COALESCE(p.minStockLevel, 0)
    ORDER BY b.remainingQuantity ASC, b.expirationDate ASC
    """)
    List<InventoryBatch> findLowStockBatches(Pageable pageable);
}
