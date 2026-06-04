package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.FinanceAuditAction;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceTransactionAuditLogResponseDTO {
    private Long id;
    private Long transactionId;
    private FinanceAuditAction action;
    private FinanceTransactionStatus oldStatus;
    private FinanceTransactionStatus newStatus;
    private String oldValueJson;
    private String newValueJson;
    private Long performedById;
    private String performedByName;
    private LocalDateTime performedAt;
    private String ipAddress;
    private String userAgent;
    private String note;
}
