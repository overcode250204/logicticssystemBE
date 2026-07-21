package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.InventoryTransaction;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.enums.InventoryBatchStatus;
import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.InventoryTransactionRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryDataSeeder implements DataSeeder {

    private final ProductRepository productRepository;
    private final InventoryBatchRepository inventoryBatchRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Override
    public int getOrder() {
        return 13;
    }

    @Override
    @Transactional
    public void seed() {
        List<BatchSeed> batches = List.of(
                new BatchSeed("PRD-FOOD-001", "SEED-BATCH-0001", 200, 180, 180, InventoryBatchStatus.GOOD),
                new BatchSeed("PRD-FOOD-002", "SEED-BATCH-0002", 120, 95, 120, InventoryBatchStatus.NORMAL),
                new BatchSeed("PRD-FOOD-003", "SEED-BATCH-0003", 150, 9, 365, InventoryBatchStatus.LOW_STOCK),
                new BatchSeed("PRD-FOOD-004", "SEED-BATCH-0004", 100, 0, 180, InventoryBatchStatus.OUT_OF_STOCK),
                new BatchSeed("PRD-BEV-001", "SEED-BATCH-0005", 90, 70, 5, InventoryBatchStatus.EXPIRING_SOON),
                new BatchSeed("PRD-BEV-002", "SEED-BATCH-0006", 80, 65, 120, InventoryBatchStatus.GOOD),
                new BatchSeed("PRD-BEV-003", "SEED-BATCH-0007", 80, 12, 90, InventoryBatchStatus.LOW_STOCK),
                new BatchSeed("PRD-BEV-004", "SEED-BATCH-0008", 60, 45, 7, InventoryBatchStatus.EXPIRING_SOON),
                new BatchSeed("PRD-HOUSE-001", "SEED-BATCH-0009", 50, 42, 540, InventoryBatchStatus.NORMAL),
                new BatchSeed("PRD-HOUSE-002", "SEED-BATCH-0010", 70, 55, 365, InventoryBatchStatus.GOOD)
        );

        batches.forEach(this::createBatchAndTransactions);
        log.info("Inventory batch and transaction seed data completed.");
    }

    private void createBatchAndTransactions(BatchSeed seed) {
        Product product = productRepository.findByProductCode(seed.productCode())
                .orElseThrow(() -> new IllegalStateException("Product not found: " + seed.productCode()));

        InventoryBatch batch = inventoryBatchRepository.findByBarcode(seed.barcode())
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    InventoryBatch created = new InventoryBatch();
                    created.setProduct(product);
                    created.setBarcode(seed.barcode());
                    created.setBarcodeImageUrl("seed://inventory/" + seed.barcode());
                    created.setImportDate(now.minusDays(30));
                    created.setReceivedAt(now.minusDays(29));
                    created.setExpirationDate(now.plusDays(seed.expiresInDays()));
                    created.setQuantity(seed.quantity());
                    created.setRemainingQuantity(seed.remainingQuantity());
                    created.setReceived(true);
                    created.setStatus(seed.status());
                    return inventoryBatchRepository.save(created);
                });

        createTransaction(batch, InventoryTransactionType.IMPORT, batch.getQuantity(), batch.getImportDate());
        int exportedQuantity = batch.getQuantity() - batch.getRemainingQuantity();
        if (exportedQuantity > 0) {
            createTransaction(batch, InventoryTransactionType.EXPORT, exportedQuantity, LocalDateTime.now().minusDays(2));
        }
    }

    private void createTransaction(
            InventoryBatch batch,
            InventoryTransactionType type,
            int quantity,
            LocalDateTime createdAt
    ) {
        if (inventoryTransactionRepository.existsByBatch_BatchIdAndType(batch.getBatchId(), type)) {
            return;
        }

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setBatch(batch);
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setCreatedAt(createdAt);
        inventoryTransactionRepository.save(transaction);
    }

    private record BatchSeed(
            String productCode,
            String barcode,
            int quantity,
            int remainingQuantity,
            int expiresInDays,
            InventoryBatchStatus status
    ) {
    }
}
