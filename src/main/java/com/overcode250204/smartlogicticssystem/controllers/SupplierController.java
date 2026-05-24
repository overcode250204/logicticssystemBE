package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.SupplierDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.services.ISupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Slf4j
public class SupplierController extends BaseCrudController<SupplierDTO, Integer> {

    private final ISupplierService supplierService;

    public SupplierController(ISupplierService supplierService) {
        super(supplierService);
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<InventoryDTO>>> getAllSuppliers() {
        try {
            return success(supplierService.getAllSupplierResponses(), "All suppliers retrieved successfully");
        } catch (Exception ex) {
            log.error("Failed to get all suppliers", ex);
            throw ex;
        }
    }
}
