package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.SupplierDTO;
import com.overcode250204.smartlogicticssystem.services.ISupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController extends BaseCrudController<SupplierDTO, Integer> {

    private final ISupplierService supplierService;

    public SupplierController(ISupplierService supplierService) {
        super(supplierService);
        this.supplierService = supplierService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<SupplierDTO>>> getAllSuppliers() {
        return success(supplierService.getAllSuppliers(), "All suppliers retrieved successfully");
    }
}
