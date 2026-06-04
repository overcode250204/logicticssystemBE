package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.FinanceTransactionAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceTransactionAuditLogRepository extends JpaRepository<FinanceTransactionAuditLog, Long> {
    List<FinanceTransactionAuditLog> findByTransaction_IdOrderByPerformedAtDesc(Long transactionId);
}
