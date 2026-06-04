package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionRejectRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionAuditLogResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PresignedUrlResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IFinanceTransactionService {
    Page<FinanceTransactionResponseDTO> search(String keyword, FinanceTransactionType type,
                                               FinanceTransactionStatus status, LocalDate fromDate,
                                               LocalDate toDate, int page, int size, int roleId, int userId);

    FinanceTransactionResponseDTO create(FinanceTransactionCreateRequest request, int roleId, int userId,
                                          String ipAddress, String userAgent);

    FinanceTransactionResponseDTO getById(Long id, int roleId, int userId);

    FinanceTransactionResponseDTO update(Long id, FinanceTransactionUpdateRequest request, int roleId, int userId,
                                          String ipAddress, String userAgent);

    void delete(Long id, int roleId, int userId, String ipAddress, String userAgent);

    FinanceTransactionResponseDTO approve(Long id, int roleId, int userId, String ipAddress, String userAgent);

    FinanceTransactionResponseDTO reject(Long id, FinanceTransactionRejectRequest request, int roleId, int userId,
                                         String ipAddress, String userAgent);

    List<FinanceTransactionAuditLogResponseDTO> getAuditLogs(Long id, int roleId, int userId);

    PresignedUrlResponseDTO createReceiptPresignedUrl(Long id, int roleId, int userId, String ipAddress, String userAgent);
}
