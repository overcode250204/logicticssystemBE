package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.FinanceTransaction;
import org.springframework.stereotype.Component;

@Component
public class FinanceTransactionMapper {
    public FinanceTransaction toEntity(FinanceTransactionCreateRequest request) {
        if (request == null) {
            return null;
        }

        FinanceTransaction transaction = new FinanceTransaction();
        transaction.setCode(request.getCode());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setDescription(request.getDescription());
        transaction.setReceiptImageKey(request.getReceiptImageKey());
        return transaction;
    }

    public FinanceTransactionResponseDTO toResponse(FinanceTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        return FinanceTransactionResponseDTO.builder()
                .id(transaction.getId())
                .code(transaction.getCode())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .paymentMethod(transaction.getPaymentMethod())
                .description(transaction.getDescription())
                .status(transaction.getStatus())
                .receiptImageKey(transaction.getReceiptImageKey())
                .createdById(transaction.getCreatedBy() != null ? transaction.getCreatedBy().getUserId() : null)
                .createdByName(transaction.getCreatedBy() != null ? transaction.getCreatedBy().getFullName() : null)
                .approvedById(transaction.getApprovedBy() != null ? transaction.getApprovedBy().getUserId() : null)
                .approvedByName(transaction.getApprovedBy() != null ? transaction.getApprovedBy().getFullName() : null)
                .createdAt(transaction.getCreatedAt())
                .approvedAt(transaction.getApprovedAt())
                .rejectReason(transaction.getRejectReason())
                .isActive(transaction.getIsActive())
                .build();
    }
}
