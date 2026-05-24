package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
@Slf4j
public class InventoryTransactionController extends BaseCrudController<InventoryTransactionDTO, Long> {

    private final IInventoryTransactionService transactionService;

    public InventoryTransactionController(IInventoryTransactionService transactionService) {
        super(transactionService);
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<InventoryDTO>>> getAllTransactions() {
        try {
            return success(transactionService.getAllTransactionResponses(), "All inventory transactions retrieved successfully");
        } catch (Exception ex) {
            log.error("Failed to get all inventory transactions", ex);
            throw ex;
        }
    }

}

