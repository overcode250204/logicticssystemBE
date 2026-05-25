package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE MONTH(i.createdAt) = :month AND YEAR(i.createdAt) = :year")
    BigDecimal sumTotalAmountByMonth(int month, int year);
}
