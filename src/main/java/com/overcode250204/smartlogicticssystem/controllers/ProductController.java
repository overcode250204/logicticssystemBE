package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import com.overcode250204.smartlogicticssystem.services.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController extends BaseCrudController<ProductDTO, Long> {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        super(productService);
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<InventoryDTO>>> getAllProducts() {
        try {
            return success(productService.getAllProductResponses(), "All products retrieved successfully");
        } catch (Exception ex) {
            log.error("Failed to get all products", ex);
            throw ex;
        }
    }

    @GetMapping("/code/{productCode}")
    public ResponseEntity<BaseResponse<ProductDTO>> getByProductCode(@PathVariable String productCode) {
        return success(productService.getByProductCode(productCode), "Product retrieved successfully");
    }
}
