package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionAuditLogResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.FinanceTransaction;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.enums.FinanceAuditAction;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;

import java.util.List;

public interface IFinanceTransactionAuditLogService {
    void log(FinanceTransaction transaction, FinanceAuditAction action,
             FinanceTransactionStatus oldStatus, FinanceTransactionStatus newStatus,
             Object oldValue, Object newValue, User performedBy,
             String ipAddress, String userAgent, String note);

    List<FinanceTransactionAuditLogResponseDTO> getLogs(Long transactionId);
}
