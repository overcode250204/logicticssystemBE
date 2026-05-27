package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryBatchUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryExportRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryBatchBarcodeResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryBatchResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryExportResponseDTO;

import java.util.List;

public interface IInventoryBatchService{
    InventoryBatchResponseDTO create(InventoryBatchCreateRequest request, int roleId, int userId);

    InventoryBatchResponseDTO update(Long id, InventoryBatchUpdateRequest request, int roleId, int userId);



    InventoryBatchResponseDTO getById(Long id, int roleId, int userId);

    InventoryBatchBarcodeResponseDTO getByBarcode(String barcode);

    void delete(Long id, int roleId, int userId);

    InventoryExportResponseDTO exportStock(InventoryExportRequest request);

    List<InventoryBatchResponseDTO> getAllBatches();

    List<InventoryBatchResponseDTO> getBatchesByProductName(String productName);

    List<InventoryBatchResponseDTO> getBatchesBySupplierName(String supplierName);
}
