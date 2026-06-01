package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.response.FinancialSummaryResponseDTO;

public interface IFinancialMetricsService {
    FinancialSummaryResponseDTO getSummary();

    FinancialSummaryResponseDTO getSummary(Integer month, Integer year);
}
