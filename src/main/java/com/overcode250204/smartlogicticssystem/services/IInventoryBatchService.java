package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.dtos.request.ExportStockRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ExportStockResponse;

import java.util.List;

public interface IInventoryBatchService extends BaseService<InventoryBatchDTO, Long> {

    ExportStockResponse exportStock(ExportStockRequest request);


    List<InventoryBatchDTO> getAllBatches();

    List<InventoryBatchDTO> getBatchesByProductName(String productName);

    List<InventoryBatchDTO> getBatchesBySupplierName(String supplierName);
}
