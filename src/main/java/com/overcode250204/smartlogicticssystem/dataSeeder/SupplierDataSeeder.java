package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupplierDataSeeder implements DataSeeder {

    private final SupplierRepository supplierRepository;

    @Override
    public int getOrder() {
        return 3; // Chạy sau Role
    }

    @Override
    @Transactional
    public void seed() {

        createSupplierIfNotExists(
                "Công ty TNHH Vinamilk",
                "0901234567",
                "10 Tân Trào, Quận 7, TP.HCM"
        );

        createSupplierIfNotExists(
                "Công ty CP Masan Consumer",
                "0902345678",
                "Tân Phú, TP.HCM"
        );

        createSupplierIfNotExists(
                "Công ty Acecook Việt Nam",
                "0903456789",
                "KCN Tân Bình, TP.HCM"
        );

        createSupplierIfNotExists(
                "Công ty Unilever Việt Nam",
                "0904567890",
                "Quận 7, TP.HCM"
        );

        createSupplierIfNotExists(
                "Công ty Nestlé Việt Nam",
                "0905678901",
                "Biên Hòa, Đồng Nai"
        );

        log.info("Supplier seed data completed.");
    }

    private void createSupplierIfNotExists(
            String supplierName,
            String contactPhone,
            String address
    ) {
        boolean exists = supplierRepository.existsBySupplierName(supplierName);

        if (exists) {
            log.info("Supplier already exists: {}", supplierName);
            return;
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierName);
        supplier.setContactPhone(contactPhone);
        supplier.setAddress(address);

        supplierRepository.save(supplier);

        log.info("Created supplier: {}", supplierName);
    }
}