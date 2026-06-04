package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionAuditLogResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.FinanceTransactionAuditLog;
import org.springframework.stereotype.Component;

@Component
public class FinanceTransactionAuditLogMapper {
    public FinanceTransactionAuditLogResponseDTO toResponse(FinanceTransactionAuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        return FinanceTransactionAuditLogResponseDTO.builder()
                .id(auditLog.getId())
                .transactionId(auditLog.getTransaction() != null ? auditLog.getTransaction().getId() : null)
                .action(auditLog.getAction())
                .oldStatus(auditLog.getOldStatus())
                .newStatus(auditLog.getNewStatus())
                .oldValueJson(auditLog.getOldValueJson())
                .newValueJson(auditLog.getNewValueJson())
                .performedById(auditLog.getPerformedBy() != null ? auditLog.getPerformedBy().getUserId() : null)
                .performedByName(auditLog.getPerformedBy() != null ? auditLog.getPerformedBy().getFullName() : null)
                .performedAt(auditLog.getPerformedAt())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .note(auditLog.getNote())
                .build();
    }
}
