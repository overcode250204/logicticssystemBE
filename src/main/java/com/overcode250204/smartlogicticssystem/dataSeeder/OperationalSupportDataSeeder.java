package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.Invoice;
import com.overcode250204.smartlogicticssystem.entities.Notification;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.enums.NotificationType;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.InvoiceRepository;
import com.overcode250204.smartlogicticssystem.repositories.NotificationRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperationalSupportDataSeeder implements DataSeeder {

    private final NotificationRepository notificationRepository;
    private final InvoiceRepository invoiceRepository;
    private final InventoryBatchRepository inventoryBatchRepository;
    private final PalletRepository palletRepository;
    private final UserRepository userRepository;

    @Override
    public int getOrder() {
        return 18;
    }

    @Override
    @Transactional
    public void seed() {
        User warehouseKeeper = requireUser("thukho@logistics.com");
        User staff = requireUser("staff1@logistics.com");
        User admin = requireUser("admin@logistics.com");

        InventoryBatch lowStock = requireBatch("SEED-BATCH-0003");
        InventoryBatch outOfStock = requireBatch("SEED-BATCH-0004");
        InventoryBatch deducted = requireBatch("SEED-BATCH-0006");
        Pallet palletTask = palletRepository.findByPalletCode("PL-SEED-0002")
                .orElseThrow(() -> new IllegalStateException("Seed pallet not found"));

        createNotification(warehouseKeeper, "Low stock warning",
                "A seeded inventory batch is below its minimum stock level.",
                NotificationType.LOW_STOCK, "INVENTORY_BATCH", lowStock.getBatchId(), false);
        createNotification(warehouseKeeper, "Out of stock warning",
                "A seeded inventory batch has no remaining quantity.",
                NotificationType.OUT_OF_STOCK, "INVENTORY_BATCH", outOfStock.getBatchId(), false);
        createNotification(warehouseKeeper, "Inventory export completed",
                "Inventory was deducted for a seeded order scenario.",
                NotificationType.INVENTORY_DEDUCTED, "INVENTORY_BATCH", deducted.getBatchId(), true);
        createNotification(staff, "Palletization task ready",
                "A seeded pallet is ready for barcode scanning.",
                NotificationType.PALLETIZATION_TASK, "PALLET", palletTask.getPalletId(), false);

        createInvoice("CUSTOMER_ORDER", new BigDecimal("1285000"), admin);
        createInvoice("INVENTORY_IMPORT", new BigDecimal("115000000"), admin);
        log.info("Notification and generic-invoice seed data completed.");
    }

    private void createNotification(
            User recipient,
            String title,
            String message,
            NotificationType type,
            String referenceType,
            Long referenceId,
            boolean read
    ) {
        if (notificationRepository.existsByRecipientIdAndReferenceTypeAndReferenceIdAndType(
                recipient.getUserId(), referenceType, referenceId, type)) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipientId(recipient.getUserId());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setIsRead(read);
        notification.setReadAt(read ? LocalDateTime.now().minusHours(1) : null);
        notificationRepository.save(notification);
    }

    private void createInvoice(String type, BigDecimal totalAmount, User admin) {
        if (invoiceRepository.existsByInvoiceTypeAndCreatedBy_UserId(type, admin.getUserId())) {
            return;
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceType(type);
        invoice.setTotalAmount(totalAmount);
        invoice.setCreatedBy(admin);
        invoiceRepository.save(invoice);
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seed user not found: " + email));
    }

    private InventoryBatch requireBatch(String barcode) {
        return inventoryBatchRepository.findByBarcode(barcode)
                .orElseThrow(() -> new IllegalStateException("Seed inventory batch not found: " + barcode));
    }
}
