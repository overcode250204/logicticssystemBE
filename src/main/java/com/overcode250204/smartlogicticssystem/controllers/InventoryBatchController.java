package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.services.IInventoryBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-batches")
public class InventoryBatchController extends BaseCrudController<InventoryBatchDTO, Long> {
    
    private final IInventoryBatchService inventoryBatchService;

    public InventoryBatchController(IInventoryBatchService service) {
        super(service);
        this.inventoryBatchService = service;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<InventoryBatchDTO>>> getAll() {
        return success(inventoryBatchService.getAll(), "Get all inventory batches successfully");
    }
}
