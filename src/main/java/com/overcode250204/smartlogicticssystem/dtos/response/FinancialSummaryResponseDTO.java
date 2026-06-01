package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryResponseDTO {
    private BigDecimal gmv;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netProfit;
    private BigDecimal supplierDebt;
    private Long pendingIncomeCount;
}
