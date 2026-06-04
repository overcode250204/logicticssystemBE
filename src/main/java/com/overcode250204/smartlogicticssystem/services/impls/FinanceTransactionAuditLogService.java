package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionAuditLogResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.FinanceTransaction;
import com.overcode250204.smartlogicticssystem.entities.FinanceTransactionAuditLog;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.enums.FinanceAuditAction;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.mapper.FinanceTransactionAuditLogMapper;
import com.overcode250204.smartlogicticssystem.repositories.FinanceTransactionAuditLogRepository;
import com.overcode250204.smartlogicticssystem.services.IFinanceTransactionAuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceTransactionAuditLogService implements IFinanceTransactionAuditLogService {

    private final FinanceTransactionAuditLogRepository auditLogRepository;
    private final FinanceTransactionAuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void log(FinanceTransaction transaction, FinanceAuditAction action,
                    FinanceTransactionStatus oldStatus, FinanceTransactionStatus newStatus,
                    Object oldValue, Object newValue, User performedBy,
                    String ipAddress, String userAgent, String note) {
        FinanceTransactionAuditLog auditLog = new FinanceTransactionAuditLog();
        auditLog.setTransaction(transaction);
        auditLog.setAction(action);
        auditLog.setOldStatus(oldStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setOldValueJson(toJson(oldValue));
        auditLog.setNewValueJson(toJson(newValue));
        auditLog.setPerformedBy(performedBy);
        auditLog.setPerformedAt(LocalDateTime.now());
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setNote(note);
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<FinanceTransactionAuditLogResponseDTO> getLogs(Long transactionId) {
        return auditLogRepository.findByTransaction_IdOrderByPerformedAtDesc(transactionId)
                .stream()
                .map(auditLogMapper::toResponse)
                .toList();
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            log.warn("Cannot serialize finance audit value", e);
            return null;
        }
    }
}
