package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-batches")
public class InventoryBatchController extends BaseCrudController<InventoryBatchDTO, Long> {

    private final IInventoryBatchService batchService;

    public InventoryBatchController(IInventoryBatchService batchService) {
        super(batchService);
        this.batchService = batchService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<InventoryBatchDTO>>> getAllBatches() {
        return success(batchService.getAllBatches(), "All inventory batches retrieved successfully");
    }

    @GetMapping("/search/product")
    public ResponseEntity<BaseResponse<List<InventoryBatchDTO>>> getBatchesByProductName(@RequestParam String name) {
        return success(batchService.getBatchesByProductName(name), "Inventory batches retrieved by product name");
    }

    @GetMapping("/search/supplier")
    public ResponseEntity<BaseResponse<List<InventoryBatchDTO>>> getBatchesBySupplierName(@RequestParam String name) {
        return success(batchService.getBatchesBySupplierName(name), "Inventory batches retrieved by supplier name");
    }
}
