package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryTransactionController extends BaseCrudController<InventoryTransactionDTO, Long> {

    public InventoryTransactionController(IInventoryTransactionService transactionService) {
        super(transactionService);
    }
}
