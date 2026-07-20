package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.ProductCategory;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.entities.Unit;
import com.overcode250204.smartlogicticssystem.enums.UnitType;
import com.overcode250204.smartlogicticssystem.repositories.ProductCategoryRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import com.overcode250204.smartlogicticssystem.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductDataSeeder implements DataSeeder {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final SupplierRepository supplierRepository;
    private final UnitRepository unitRepository;

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    @Transactional
    public void seed() {
        List<Supplier> suppliers = supplierRepository.findAll();
        if (suppliers.isEmpty()) {
            log.warn("Skip product seed data because no supplier exists.");
            return;
        }

        Unit pieceUnit = unitRepository.findByCodeIgnoreCaseAndType("PCS", UnitType.QUANTITY)
                .orElse(null);

        createProductIfNotExists(
                "PRD-FOOD-001",
                "GAO-ST25-5KG",
                "Gạo ST25 5kg",
                "FOOD",
                suppliers.get(0),
                pieceUnit,
                "185000", "5.000", "40.000", "30.000", "10.000", 50
        );

        createProductIfNotExists(
                "PRD-FOOD-002",
                "MI-HAOHAO-30GOI",
                "Thùng mì Hảo Hảo 30 gói",
                "FOOD",
                suppliers.get(0),
                pieceUnit,
                "145000", "3.500", "45.000", "30.000", "20.000", 40
        );

        createProductIfNotExists(
                "PRD-FOOD-003",
                "DUONG-1KG",
                "Đường trắng 1kg",
                "FOOD",
                suppliers.get(0),
                pieceUnit,
                "28000", "1.000", "20.000", "12.000", "8.000", 100
        );

        createProductIfNotExists(
                "PRD-FOOD-004",
                "MUOI-1KG",
                "Muối ăn 1kg",
                "FOOD",
                suppliers.get(0),
                pieceUnit,
                "12000", "1.000", "18.000", "10.000", "6.000", 120
        );

        createProductIfNotExists(
                "PRD-BEV-001",
                "NUOCSUOI-24CHAI",
                "Thùng nước suối Aquafina 24 chai",
                "BEVERAGE",
                suppliers.get(1),
                pieceUnit,
                "98000", "12.000", "40.000", "25.000", "30.000", 30
        );

        createProductIfNotExists(
                "PRD-BEV-002",
                "COCA-24LON",
                "Thùng Coca Cola 24 lon",
                "BEVERAGE",
                suppliers.get(1),
                pieceUnit,
                "215000", "9.000", "42.000", "28.000", "18.000", 25
        );

        createProductIfNotExists(
                "PRD-BEV-003",
                "PEPSI-24LON",
                "Thùng Pepsi 24 lon",
                "BEVERAGE",
                suppliers.get(1),
                pieceUnit,
                "205000", "9.000", "42.000", "28.000", "18.000", 25
        );

        createProductIfNotExists(
                "PRD-BEV-004",
                "SUATUOI-48HOP",
                "Thùng sữa tươi 48 hộp",
                "BEVERAGE",
                suppliers.get(1),
                pieceUnit,
                "420000", "15.000", "45.000", "35.000", "25.000", 20
        );

        createProductIfNotExists(
                "PRD-HOUSE-001",
                "BOTGIAT-OMO",
                "Bột giặt OMO 5kg",
                "HOUSEHOLD",
                suppliers.get(2),
                pieceUnit,
                "245000", "5.000", "35.000", "25.000", "30.000", 20
        );

        createProductIfNotExists(
                "PRD-HOUSE-002",
                "NUOCRUA-CHEN",
                "Nước rửa chén Sunlight",
                "HOUSEHOLD",
                suppliers.get(2),
                pieceUnit,
                "68000", "3.500", "18.000", "12.000", "30.000", 30
        );

        createProductIfNotExists(
                "PRD-HOUSE-003",
                "GIAYVESINH-10CUON",
                "Giấy vệ sinh 10 cuộn",
                "HOUSEHOLD",
                suppliers.get(2),
                pieceUnit,
                "95000", "2.500", "50.000", "25.000", "20.000", 35
        );

        createProductIfNotExists(
                "PRD-HOUSE-004",
                "NUOCLAUSAN",
                "Nước lau sàn 3.8L",
                "HOUSEHOLD",
                suppliers.get(2),
                pieceUnit,
                "125000", "4.000", "20.000", "15.000", "35.000", 20
        );

        createProductIfNotExists(
                "PRD-CARE-001",
                "DAUGOI-PANTENE",
                "Dầu gội Pantene 900ml",
                "PERSONAL_CARE",
                suppliers.get(3),
                pieceUnit,
                "185000", "1.000", "12.000", "8.000", "30.000", 25
        );

        createProductIfNotExists(
                "PRD-CARE-002",
                "KEMDANHRANG-PS",
                "Kem đánh răng P/S",
                "PERSONAL_CARE",
                suppliers.get(3),
                pieceUnit,
                "42000", "0.200", "20.000", "5.000", "5.000", 50
        );

        createProductIfNotExists(
                "PRD-CARE-003",
                "SUATAM-DOVE",
                "Sữa tắm Dove 900g",
                "PERSONAL_CARE",
                suppliers.get(3),
                pieceUnit,
                "165000", "1.000", "12.000", "8.000", "30.000", 25
        );

        createProductIfNotExists(
                "PRD-CARE-004",
                "BANCHAIRANG-ORALB",
                "Bàn chải đánh răng Oral-B",
                "PERSONAL_CARE",
                suppliers.get(3),
                pieceUnit,
                "38000", "0.100", "22.000", "5.000", "3.000", 80
        );

        createProductIfNotExists(
                "PRD-ELEC-001",
                "SAC-20W",
                "Củ sạc nhanh 20W",
                "ELECTRONICS",
                suppliers.get(4),
                pieceUnit,
                "290000", "0.300", "12.000", "8.000", "5.000", 10
        );

        createProductIfNotExists(
                "PRD-ELEC-002",
                "CAP-TYPEC",
                "Cáp sạc Type-C",
                "ELECTRONICS",
                suppliers.get(4),
                pieceUnit,
                "120000", "0.150", "15.000", "10.000", "3.000", 20
        );

        createProductIfNotExists(
                "PRD-ELEC-003",
                "TAINGHE-BLUETOOTH",
                "Tai nghe Bluetooth",
                "ELECTRONICS",
                suppliers.get(4),
                pieceUnit,
                "590000", "0.250", "18.000", "12.000", "6.000", 10
        );

        createProductIfNotExists(
                "PRD-ELEC-004",
                "PIN-DUPHONG-10000",
                "Pin sạc dự phòng 10000mAh",
                "ELECTRONICS",
                suppliers.get(4),
                pieceUnit,
                "450000", "0.450", "16.000", "9.000", "4.000", 15
        );

        log.info("Product seed data completed.");
    }

    private void createProductIfNotExists(
            String productCode,
            String sku,
            String productName,
            String categoryCode,
            Supplier supplier,
            Unit baseUnit,
            String price,
            String weight,
            String length,
            String width,
            String height,
            Integer minStockLevel
    ) {
        if (productRepository.existsByProductCode(productCode)) {
            log.info("Product already exists: {}", productCode);
            return;
        }

        ProductCategory category = productCategoryRepository.findByCategoryCode(categoryCode);
        if (category == null) {
            log.warn("Skip product {} because category {} does not exist.", productCode, categoryCode);
            return;
        }

        Product product = new Product();
        product.setProductCode(productCode);
        product.setSku(sku);
        product.setProductName(productName);
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setBaseUnit(baseUnit);
        product.setPrice(new BigDecimal(price));
        product.setWeight(new BigDecimal(weight));
        product.setLength(new BigDecimal(length));
        product.setWidth(new BigDecimal(width));
        product.setHeight(new BigDecimal(height));
        product.setMinStockLevel(minStockLevel);

        productRepository.save(product);

        log.info("Created product: {}", productCode);
    }
}
