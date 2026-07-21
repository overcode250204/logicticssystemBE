package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.ProductUnit;
import com.overcode250204.smartlogicticssystem.entities.Unit;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductUnitRepository;
import com.overcode250204.smartlogicticssystem.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUnitDataSeeder implements DataSeeder {

    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final UnitRepository unitRepository;

    @Override
    public int getOrder() {
        return 8;
    }

    @Override
    @Transactional
    public void seed() {
        Unit piece = requireUnit("PCS");
        Unit box = requireUnit("BOX");

        productRepository.findAll().forEach(product -> {
            createProductUnit(product, piece, BigDecimal.ONE);
            createProductUnit(product, box, boxFactor(product));
        });

        log.info("Product-unit conversion seed data completed.");
    }

    private void createProductUnit(Product product, Unit unit, BigDecimal conversionFactor) {
        if (productUnitRepository.existsByProduct_ProductIdAndUnit_Id(product.getProductId(), unit.getId())) {
            return;
        }

        ProductUnit productUnit = new ProductUnit();
        productUnit.setProduct(product);
        productUnit.setUnit(unit);
        productUnit.setConversionFactor(conversionFactor);
        productUnitRepository.save(productUnit);
    }

    private BigDecimal boxFactor(Product product) {
        String code = product.getProductCode();
        if (code != null && code.startsWith("PRD-BEV")) {
            return new BigDecimal("24");
        }
        if (code != null && code.startsWith("PRD-FOOD")) {
            return new BigDecimal("20");
        }
        return new BigDecimal("12");
    }

    private Unit requireUnit(String code) {
        return unitRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalStateException("Required unit not found: " + code));
    }
}
