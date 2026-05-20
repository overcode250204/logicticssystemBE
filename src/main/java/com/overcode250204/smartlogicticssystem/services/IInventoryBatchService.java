package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import java.util.List;

public interface IInventoryBatchService {
    InventoryBatchDTO createBatch(InventoryBatchDTO dto);
    List<InventoryBatchDTO> getAllBatches();
    List<InventoryBatchDTO> getBatchesByProductName(String productName);
    List<InventoryBatchDTO> getBatchesBySupplierName(String supplierName);
}
