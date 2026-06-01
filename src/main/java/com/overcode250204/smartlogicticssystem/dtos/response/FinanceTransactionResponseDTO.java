package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import com.overcode250204.smartlogicticssystem.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceTransactionResponseDTO {
    private Long id;
    private String code;
    private FinanceTransactionType type;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private PaymentMethod paymentMethod;
    private String description;
    private FinanceTransactionStatus status;
    private String receiptImageKey;
    private Long createdById;
    private String createdByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String rejectReason;
    private Boolean isActive;
}
