package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.SupplierInvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierInvoicePaymentRepository extends JpaRepository<SupplierInvoicePayment, Long> {
    List<SupplierInvoicePayment> findByInvoice_IdOrderByPaymentDateDescCreatedAtDesc(Long invoiceId);
}
