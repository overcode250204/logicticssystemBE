package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

    List<InventoryBatch> findByProductProductNameContainingIgnoreCase(String productName);
    List<InventoryBatch> findByProductSupplierSupplierNameContainingIgnoreCase(String supplierName);
}
