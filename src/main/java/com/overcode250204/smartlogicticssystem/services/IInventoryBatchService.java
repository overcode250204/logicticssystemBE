package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;

import java.util.List;

public interface IInventoryBatchService extends BaseService<InventoryBatchDTO, Long> {

    InventoryBatchDTO exportStock(InventoryBatchDTO request);


    List<InventoryBatchDTO> getAllBatches();

    List<InventoryDTO> getAllBatchResponses();

    List<InventoryBatchDTO> getBatchesByProductName(String productName);

    List<InventoryBatchDTO> getBatchesBySupplierName(String supplierName);
}
