package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.enums.InventoryBatchStatus;
import com.overcode250204.smartlogicticssystem.enums.InvoiceStatus;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.enums.LocalTripDetailStatus;
import com.overcode250204.smartlogicticssystem.enums.LocalTripStatus;
import com.overcode250204.smartlogicticssystem.enums.NotificationType;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import com.overcode250204.smartlogicticssystem.repositories.CustomerAddressRepository;
import com.overcode250204.smartlogicticssystem.repositories.DriverRepository;
import com.overcode250204.smartlogicticssystem.repositories.ExceptionReasonRepository;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.InventoryTransactionRepository;
import com.overcode250204.smartlogicticssystem.repositories.InvoiceRepository;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripDriverRepository;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.LocalTripDetailRepository;
import com.overcode250204.smartlogicticssystem.repositories.LocalTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.NotificationRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderItemRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderExceptionRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderTrackingRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletItemRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductUnitRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteProvinceRepository;
import com.overcode250204.smartlogicticssystem.repositories.RoleRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierInvoicePaymentRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierInvoiceRepository;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import com.overcode250204.smartlogicticssystem.repositories.UnitRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import com.overcode250204.smartlogicticssystem.repositories.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedVerificationDataSeeder implements DataSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final UnitRepository unitRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZoneRepository zoneRepository;
    private final RouteConfigRepository routeConfigRepository;
    private final RouteProvinceRepository routeProvinceRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ProductRepository productRepository;
    private final ProductUnitRepository productUnitRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final InventoryBatchRepository inventoryBatchRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ExceptionReasonRepository exceptionReasonRepository;
    private final SupplierInvoiceRepository supplierInvoiceRepository;
    private final SupplierInvoicePaymentRepository supplierInvoicePaymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTrackingRepository orderTrackingRepository;
    private final OrderExceptionRepository orderExceptionRepository;
    private final PalletRepository palletRepository;
    private final PalletItemRepository palletItemRepository;
    private final LinehaulTripRepository linehaulTripRepository;
    private final LinehaulTripDriverRepository linehaulTripDriverRepository;
    private final LocalTripRepository localTripRepository;
    private final LocalTripDetailRepository localTripDetailRepository;
    private final NotificationRepository notificationRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    @Transactional(readOnly = true)
    public void seed() {
        verifyMinimum("roles", roleRepository.count(), 5);
        verifyMinimum("users", userRepository.count(), 13);
        verifyMinimum("suppliers", supplierRepository.count(), 5);
        verifyMinimum("units", unitRepository.count(), 9);
        verifyMinimum("warehouses", warehouseRepository.count(), 4);
        verifyMinimum("zones", zoneRepository.count(), 4);
        verifyMinimum("route configs", routeConfigRepository.count(), 2);
        verifyMinimum("route provinces", routeProvinceRepository.count(), 3);
        verifyMinimum("vehicles", vehicleRepository.count(), 7);
        verifyMinimum("drivers", driverRepository.count(), 6);
        verifyMinimum("products", productRepository.count(), 20);
        verifyMinimum("product units", productUnitRepository.count(), 40);
        verifyMinimum("customer addresses", customerAddressRepository.count(), 6);
        verifyMinimum("inventory batches", inventoryBatchRepository.count(), 10);
        verifyMinimum("inventory transactions", inventoryTransactionRepository.count(), 20);
        verifyMinimum("exception reasons", exceptionReasonRepository.count(), 8);
        verifyMinimum("supplier invoices", supplierInvoiceRepository.count(), 4);
        verifyMinimum("supplier invoice payments", supplierInvoicePaymentRepository.count(), 2);
        verifyMinimum("orders", orderRepository.count(), 11);
        verifyMinimum("order items", orderItemRepository.count(), 22);
        verifyMinimum("order tracking records", orderTrackingRepository.count(), 20);
        verifyMinimum("order exceptions", orderExceptionRepository.count(), 1);
        verifyMinimum("pallets", palletRepository.count(), 5);
        verifyMinimum("pallet items", palletItemRepository.count(), 5);
        verifyMinimum("linehaul trips", linehaulTripRepository.count(), 5);
        verifyMinimum("linehaul driver assignments", linehaulTripDriverRepository.count(), 10);
        verifyMinimum("local trips", localTripRepository.count(), 6);
        verifyMinimum("local trip details", localTripDetailRepository.count(), 4);
        verifyMinimum("notifications", notificationRepository.count(), 4);
        verifyMinimum("generic invoices", invoiceRepository.count(), 2);

        verifyStatuses("inventory batch", statuses(
                inventoryBatchRepository.findAll().stream().map(batch -> batch.getStatus()).collect(Collectors.toSet())),
                InventoryBatchStatus.values());
        verifyStatuses("supplier invoice", statuses(
                supplierInvoiceRepository.findAll().stream().map(invoice -> invoice.getStatus()).collect(Collectors.toSet())),
                InvoiceStatus.values());
        verifyStatuses("order", statuses(
                orderRepository.findAll().stream().map(order -> order.getStatus()).collect(Collectors.toSet())),
                OrderStatus.values());
        verifyStatuses("pallet", statuses(
                palletRepository.findAll().stream().map(pallet -> pallet.getStatus()).collect(Collectors.toSet())),
                PalletStatus.values());
        verifyStatuses("linehaul trip", statuses(
                linehaulTripRepository.findAll().stream().map(trip -> trip.getStatus()).collect(Collectors.toSet())),
                LinehaulTripStatus.values());
        verifyStatuses("local trip", statuses(
                localTripRepository.findAll().stream().map(trip -> trip.getStatus()).collect(Collectors.toSet())),
                LocalTripStatus.values());
        verifyStatuses("local trip detail", statuses(
                localTripDetailRepository.findAll().stream().map(detail -> detail.getStatus()).collect(Collectors.toSet())),
                LocalTripDetailStatus.values());
        verifyStatuses("notification", statuses(
                notificationRepository.findAll().stream().map(notification -> notification.getType()).collect(Collectors.toSet())),
                NotificationType.values());

        log.info("Seed verification passed: all required aggregates and status scenarios are present.");
    }

    private void verifyMinimum(String entityName, long actual, long expectedMinimum) {
        if (actual < expectedMinimum) {
            throw new IllegalStateException(
                    "Seed verification failed for %s: expected at least %d, found %d"
                            .formatted(entityName, expectedMinimum, actual)
            );
        }
    }

    private <T extends Enum<T>> void verifyStatuses(String entityName, Set<T> actual, T[] expected) {
        Set<T> missing = Arrays.stream(expected)
                .filter(status -> !actual.contains(status))
                .collect(Collectors.toSet());
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Seed verification failed for %s statuses. Missing: %s".formatted(entityName, missing)
            );
        }
    }

    private <T extends Enum<T>> Set<T> statuses(Set<T> values) {
        values.remove(null);
        return values;
    }
}
