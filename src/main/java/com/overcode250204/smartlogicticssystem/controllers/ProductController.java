package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.ProductDTO;
import com.overcode250204.smartlogicticssystem.services.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseCrudController<ProductDTO, Long> {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        super(productService);
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductDTO>>> getAllProducts() {
        return success(productService.getAllProducts(), "All products retrieved successfully");
    }

    @GetMapping("/code/{productCode}")
    public ResponseEntity<BaseResponse<ProductDTO>> getByProductCode(@PathVariable String productCode) {
        return success(productService.getByProductCode(productCode), "Product retrieved successfully");
    }
}
