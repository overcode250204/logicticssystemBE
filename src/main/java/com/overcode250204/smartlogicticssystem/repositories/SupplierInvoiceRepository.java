package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.SupplierInvoice;
import com.overcode250204.smartlogicticssystem.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SupplierInvoiceRepository extends JpaRepository<SupplierInvoice, Long> {
    boolean existsByInvoiceCode(String invoiceCode);

    Optional<SupplierInvoice> findByInvoiceCode(String invoiceCode);

    boolean existsByInvoiceCodeAndIdNot(String invoiceCode, Long id);

    Optional<SupplierInvoice> findByIdAndIsActiveTrue(Long id);

    @Query("""
            SELECT invoice FROM SupplierInvoice invoice
            WHERE invoice.isActive = true
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(invoice.invoiceCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(invoice.note) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(invoice.supplier.supplierName) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:supplierId IS NULL OR invoice.supplier.supplierId = :supplierId)
              AND (:status IS NULL OR invoice.status = :status)
              AND (:fromDate IS NULL OR invoice.invoiceDate >= :fromDate)
              AND (:toDate IS NULL OR invoice.invoiceDate <= :toDate)
            """)
    Page<SupplierInvoice> search(
            @Param("keyword") String keyword,
            @Param("supplierId") Integer supplierId,
            @Param("status") InvoiceStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}
