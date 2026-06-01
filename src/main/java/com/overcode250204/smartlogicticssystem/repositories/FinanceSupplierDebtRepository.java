package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.SupplierInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface FinanceSupplierDebtRepository extends JpaRepository<SupplierInvoice, Long> {
    @Query("""
            SELECT COALESCE(SUM(invoice.remainingAmount), 0)
            FROM SupplierInvoice invoice
            WHERE invoice.isActive = true
              AND invoice.remainingAmount > 0
            """)
    BigDecimal sumActiveRemainingAmount();
}
