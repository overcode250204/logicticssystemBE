package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.response.FinancialSummaryResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import com.overcode250204.smartlogicticssystem.repositories.FinanceSupplierDebtRepository;
import com.overcode250204.smartlogicticssystem.repositories.FinanceTransactionRepository;
import com.overcode250204.smartlogicticssystem.services.IFinancialMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class FinancialMetricsService implements IFinancialMetricsService {
    private final FinanceTransactionRepository transactionRepository;
    private final FinanceSupplierDebtRepository supplierDebtRepository;

    @Override
    public FinancialSummaryResponseDTO getSummary() {
        return getSummary(null, null);
    }

    @Override
    public FinancialSummaryResponseDTO getSummary(Integer month, Integer year) {
        LocalDate fromDate = resolveFromDate(month, year);
        LocalDate toDate = resolveToDate(month, year);

        BigDecimal totalIncome;
        BigDecimal totalExpense;
        long pendingIncomeCount;

        if (fromDate == null || toDate == null) {
            totalIncome = transactionRepository.sumAmountByTypeAndStatus(
                    FinanceTransactionType.INCOME, FinanceTransactionStatus.APPROVED);
            totalExpense = transactionRepository.sumAmountByTypeAndStatus(
                    FinanceTransactionType.EXPENSE, FinanceTransactionStatus.APPROVED);
            pendingIncomeCount = transactionRepository.countByTypeAndStatusAndIsActiveTrue(
                    FinanceTransactionType.INCOME, FinanceTransactionStatus.PENDING);
        } else {
            totalIncome = transactionRepository.sumAmountByTypeAndStatusAndDateRange(
                    FinanceTransactionType.INCOME, FinanceTransactionStatus.APPROVED, fromDate, toDate);
            totalExpense = transactionRepository.sumAmountByTypeAndStatusAndDateRange(
                    FinanceTransactionType.EXPENSE, FinanceTransactionStatus.APPROVED, fromDate, toDate);
            pendingIncomeCount = transactionRepository.countByTypeAndStatusAndDateRange(
                    FinanceTransactionType.INCOME, FinanceTransactionStatus.PENDING, fromDate, toDate);
        }

        BigDecimal supplierDebt = supplierDebtRepository.sumActiveRemainingAmount();

        return FinancialSummaryResponseDTO.builder()
                .gmv(BigDecimal.ZERO)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netProfit(totalIncome.subtract(totalExpense))
                .supplierDebt(supplierDebt)
                .pendingIncomeCount(pendingIncomeCount)
                .build();
    }

    private LocalDate resolveFromDate(Integer month, Integer year) {
        if (year == null) {
            return null;
        }
        if (month == null) {
            return LocalDate.of(year, 1, 1);
        }
        return YearMonth.of(year, month).atDay(1);
    }

    private LocalDate resolveToDate(Integer month, Integer year) {
        if (year == null) {
            return null;
        }
        if (month == null) {
            return LocalDate.of(year, 12, 31);
        }
        return YearMonth.of(year, month).atEndOfMonth();
    }
}
