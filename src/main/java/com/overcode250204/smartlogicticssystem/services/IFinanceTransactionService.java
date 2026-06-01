package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionRejectRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface IFinanceTransactionService {
    Page<FinanceTransactionResponseDTO> search(String keyword, FinanceTransactionType type,
                                               FinanceTransactionStatus status, LocalDate fromDate,
                                               LocalDate toDate, int page, int size);

    FinanceTransactionResponseDTO create(FinanceTransactionCreateRequest request, int roleId, int userId);

    FinanceTransactionResponseDTO getById(Long id, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    FinanceTransactionResponseDTO approve(Long id, int roleId, int userId);

    FinanceTransactionResponseDTO reject(Long id, FinanceTransactionRejectRequest request, int roleId, int userId);
}
