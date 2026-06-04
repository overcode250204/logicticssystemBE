package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionRejectRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionAuditLogResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PresignedUrlResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.FinanceTransaction;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.enums.FinanceAuditAction;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.FinanceTransactionErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UserErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.FinanceTransactionMapper;
import com.overcode250204.smartlogicticssystem.repositories.FinanceTransactionRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.IFileStorageService;
import com.overcode250204.smartlogicticssystem.services.IFinanceTransactionAuditLogService;
import com.overcode250204.smartlogicticssystem.services.IFinanceTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class FinanceTransactionService extends BaseServiceImpl implements IFinanceTransactionService {
    private static final int ADMIN_ROLE_ID = 1;
    private static final int WAREHOUSE_MANAGER_ROLE_ID = 2;
    private static final DateTimeFormatter CODE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final FinanceTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final FinanceTransactionMapper transactionMapper;
    private final IFinanceTransactionAuditLogService auditLogService;
    private final IFileStorageService fileStorageService;

    @Override
    public Page<FinanceTransactionResponseDTO> search(String keyword, FinanceTransactionType type,
                                                      FinanceTransactionStatus status, LocalDate fromDate,
                                                      LocalDate toDate, int page, int size, int roleId, int userId) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "transactionDate")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt")));
        return transactionRepository.findAll(buildSearchSpecification(keyword, type, status, fromDate, toDate,
                        roleId, userId), pageable)
                .map(transactionMapper::toResponse);
    }

    @Override
    @Transactional
    public FinanceTransactionResponseDTO create(FinanceTransactionCreateRequest request, int roleId, int userId,
                                                String ipAddress, String userAgent) {
        String code = StringUtils.hasText(request.getCode()) ? request.getCode().trim() : generateTransactionCode();
        if (transactionRepository.existsByCode(code)) {
            throw new AppException(FinanceTransactionErrorCode.CODE_ALREADY_EXISTS);
        }

        User creator = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        FinanceTransaction transaction = transactionMapper.toEntity(request);
        transaction.setCode(code);
        transaction.setCreatedBy(creator);
        transaction.setIsActive(true);

        if (request.getType() == FinanceTransactionType.EXPENSE) {
            ensureAdmin(roleId);
            transaction.setStatus(FinanceTransactionStatus.APPROVED);
            transaction.setApprovedBy(creator);
            transaction.setApprovedAt(LocalDateTime.now());
        } else if (request.getType() == FinanceTransactionType.INCOME) {
            if (roleId != WAREHOUSE_MANAGER_ROLE_ID) {
                throw new AppException(FinanceTransactionErrorCode.INVALID_TRANSACTION_TYPE_FOR_ROLE);
            }
            transaction.setStatus(FinanceTransactionStatus.PENDING);
        } else {
            throw new AppException(FinanceTransactionErrorCode.INVALID_TRANSACTION_TYPE_FOR_ROLE);
        }

        FinanceTransaction savedTransaction = transactionRepository.save(transaction);
        FinanceTransactionResponseDTO response = transactionMapper.toResponse(savedTransaction);
        auditLogService.log(savedTransaction, FinanceAuditAction.CREATE, null, savedTransaction.getStatus(),
                null, response, creator, ipAddress, userAgent, "Transaction created");
        return response;
    }

    @Override
    public FinanceTransactionResponseDTO getById(Long id, int roleId, int userId) {
        FinanceTransaction transaction = getActiveTransaction(id);
        ensureCanAccess(transaction, roleId, userId);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public FinanceTransactionResponseDTO update(Long id, FinanceTransactionUpdateRequest request, int roleId, int userId,
                                                String ipAddress, String userAgent) {
        FinanceTransaction transaction = getActiveTransaction(id);
        ensureCanUpdate(transaction, roleId, userId);

        User performer = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        FinanceTransactionResponseDTO oldValue = transactionMapper.toResponse(transaction);
        FinanceTransactionStatus oldStatus = transaction.getStatus();

        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setDescription(request.getDescription());
        transaction.setReceiptImageKey(request.getReceiptImageKey());

        FinanceTransaction savedTransaction = transactionRepository.save(transaction);
        FinanceTransactionResponseDTO response = transactionMapper.toResponse(savedTransaction);
        auditLogService.log(savedTransaction, FinanceAuditAction.UPDATE, oldStatus, savedTransaction.getStatus(),
                oldValue, response, performer, ipAddress, userAgent, "Transaction updated");
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId, String ipAddress, String userAgent) {
        ensureAdmin(roleId);
        FinanceTransaction transaction = getActiveTransaction(id);
        User admin = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        FinanceTransactionResponseDTO oldValue = transactionMapper.toResponse(transaction);
        FinanceTransactionStatus oldStatus = transaction.getStatus();

        transaction.setIsActive(false);
        FinanceTransaction savedTransaction = transactionRepository.save(transaction);
        auditLogService.log(savedTransaction, FinanceAuditAction.SOFT_DELETE, oldStatus, savedTransaction.getStatus(),
                oldValue, transactionMapper.toResponse(savedTransaction), admin, ipAddress, userAgent,
                "Transaction soft deleted");
    }

    @Override
    @Transactional
    public FinanceTransactionResponseDTO approve(Long id, int roleId, int userId, String ipAddress, String userAgent) {
        ensureAdmin(roleId);
        FinanceTransaction transaction = getActiveTransaction(id);
        ensurePendingIncomeForApprove(transaction);

        User admin = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        FinanceTransactionResponseDTO oldValue = transactionMapper.toResponse(transaction);
        FinanceTransactionStatus oldStatus = transaction.getStatus();

        transaction.setStatus(FinanceTransactionStatus.APPROVED);
        transaction.setApprovedBy(admin);
        transaction.setApprovedAt(LocalDateTime.now());
        transaction.setRejectReason(null);

        FinanceTransaction savedTransaction = transactionRepository.save(transaction);
        FinanceTransactionResponseDTO response = transactionMapper.toResponse(savedTransaction);
        auditLogService.log(savedTransaction, FinanceAuditAction.APPROVE, oldStatus, savedTransaction.getStatus(),
                oldValue, response, admin, ipAddress, userAgent, "Approved by admin");
        return response;
    }

    @Override
    @Transactional
    public FinanceTransactionResponseDTO reject(Long id, FinanceTransactionRejectRequest request, int roleId, int userId,
                                                String ipAddress, String userAgent) {
        ensureAdmin(roleId);
        FinanceTransaction transaction = getActiveTransaction(id);
        ensurePendingIncomeForReject(transaction);

        User admin = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        FinanceTransactionResponseDTO oldValue = transactionMapper.toResponse(transaction);
        FinanceTransactionStatus oldStatus = transaction.getStatus();

        transaction.setStatus(FinanceTransactionStatus.REJECTED);
        transaction.setApprovedBy(admin);
        transaction.setApprovedAt(LocalDateTime.now());
        transaction.setRejectReason(request.getRejectReason());

        FinanceTransaction savedTransaction = transactionRepository.save(transaction);
        FinanceTransactionResponseDTO response = transactionMapper.toResponse(savedTransaction);
        auditLogService.log(savedTransaction, FinanceAuditAction.REJECT, oldStatus, savedTransaction.getStatus(),
                oldValue, response, admin, ipAddress, userAgent, request.getRejectReason());
        return response;
    }

    @Override
    public List<FinanceTransactionAuditLogResponseDTO> getAuditLogs(Long id, int roleId, int userId) {
        FinanceTransaction transaction = getActiveTransaction(id);
        ensureCanAccess(transaction, roleId, userId);
        return auditLogService.getLogs(id);
    }

    @Override
    @Transactional
    public PresignedUrlResponseDTO createReceiptPresignedUrl(Long id, int roleId, int userId,
                                                             String ipAddress, String userAgent) {
        FinanceTransaction transaction = getActiveTransaction(id);
        ensureCanAccess(transaction, roleId, userId);
        if (!StringUtils.hasText(transaction.getReceiptImageKey())) {
            throw new AppException(FinanceTransactionErrorCode.RECEIPT_NOT_FOUND);
        }

        User performer = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        PresignedUrlResponseDTO response = fileStorageService.createPresignedUrl(transaction.getReceiptImageKey());
        auditLogService.log(transaction, FinanceAuditAction.VIEW_RECEIPT, transaction.getStatus(), transaction.getStatus(),
                null, transactionMapper.toResponse(transaction), performer, ipAddress, userAgent, "Receipt viewed");
        return response;
    }

    private FinanceTransaction getActiveTransaction(Long id) {
        return transactionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(FinanceTransactionErrorCode.TRANSACTION_NOT_FOUND));
    }

    private Specification<FinanceTransaction> buildSearchSpecification(String keyword, FinanceTransactionType type,
                                                                       FinanceTransactionStatus status,
                                                                       LocalDate fromDate, LocalDate toDate,
                                                                       int roleId, int userId) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.equal(root.get("isActive"), true);

            if (roleId != ADMIN_ROLE_ID) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("createdBy").get("userId"), (long) userId));
            }

            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), pattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
                        ));
            }
            if (type != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), type));
            }
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (fromDate != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), fromDate));
            }
            if (toDate != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), toDate));
            }
            return predicate;
        };
    }

    private void ensureAdmin(int roleId) {
        if (roleId != ADMIN_ROLE_ID) {
            throw new AppException(FinanceTransactionErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    private void ensureCanUpdate(FinanceTransaction transaction, int roleId, int userId) {
        if (transaction.getStatus() == FinanceTransactionStatus.APPROVED && roleId != ADMIN_ROLE_ID) {
            throw new AppException(FinanceTransactionErrorCode.APPROVED_TRANSACTION_REQUIRES_ADMIN);
        }
        if (roleId == ADMIN_ROLE_ID) {
            return;
        }
        if (transaction.getCreatedBy() == null || !transaction.getCreatedBy().getUserId().equals((long) userId)) {
            throw new AppException(FinanceTransactionErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    private void ensureCanAccess(FinanceTransaction transaction, int roleId, int userId) {
        if (roleId == ADMIN_ROLE_ID) {
            return;
        }
        if (transaction.getCreatedBy() == null || !transaction.getCreatedBy().getUserId().equals((long) userId)) {
            throw new AppException(FinanceTransactionErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    private String generateTransactionCode() {
        String datePart = LocalDate.now().format(CODE_DATE_FORMATTER);
        String code;
        do {
            int randomPart = ThreadLocalRandom.current().nextInt(10000, 100000);
            code = "FT-" + datePart + "-" + randomPart;
        } while (transactionRepository.existsByCode(code));
        return code;
    }

    private void ensurePendingIncomeForApprove(FinanceTransaction transaction) {
        if (transaction.getType() != FinanceTransactionType.INCOME
                || transaction.getStatus() != FinanceTransactionStatus.PENDING) {
            throw new AppException(FinanceTransactionErrorCode.ONLY_PENDING_INCOME_CAN_BE_APPROVED);
        }
    }

    private void ensurePendingIncomeForReject(FinanceTransaction transaction) {
        if (transaction.getType() != FinanceTransactionType.INCOME
                || transaction.getStatus() != FinanceTransactionStatus.PENDING) {
            throw new AppException(FinanceTransactionErrorCode.ONLY_PENDING_INCOME_CAN_BE_REJECTED);
        }
    }
}
