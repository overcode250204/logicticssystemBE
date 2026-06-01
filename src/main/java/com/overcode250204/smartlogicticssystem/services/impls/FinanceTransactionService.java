package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.FinanceTransactionRejectRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.FinanceTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.FinanceTransaction;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.FinanceTransactionErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UserErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.FinanceTransactionMapper;
import com.overcode250204.smartlogicticssystem.repositories.FinanceTransactionRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
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

    @Override
    public Page<FinanceTransactionResponseDTO> search(String keyword, FinanceTransactionType type,
                                                      FinanceTransactionStatus status, LocalDate fromDate,
                                                      LocalDate toDate, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "transactionDate")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt")));
        return transactionRepository.findAll(buildSearchSpecification(keyword, type, status, fromDate, toDate), pageable)
                .map(transactionMapper::toResponse);
    }

    @Override
    @Transactional
    public FinanceTransactionResponseDTO create(FinanceTransactionCreateRequest request, int roleId, int userId) {
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

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public FinanceTransactionResponseDTO getById(Long id, int roleId, int userId) {
        return transactionMapper.toResponse(getActiveTransaction(id));
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        ensureAdmin(roleId);
        FinanceTransaction transaction = getActiveTransaction(id);
        transaction.setIsActive(false);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public FinanceTransactionResponseDTO approve(Long id, int roleId, int userId) {
        ensureAdmin(roleId);
        FinanceTransaction transaction = getActiveTransaction(id);
        ensurePendingIncomeForApprove(transaction);

        User admin = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        transaction.setStatus(FinanceTransactionStatus.APPROVED);
        transaction.setApprovedBy(admin);
        transaction.setApprovedAt(LocalDateTime.now());
        transaction.setRejectReason(null);

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public FinanceTransactionResponseDTO reject(Long id, FinanceTransactionRejectRequest request, int roleId, int userId) {
        ensureAdmin(roleId);
        FinanceTransaction transaction = getActiveTransaction(id);
        ensurePendingIncomeForReject(transaction);

        User admin = findByIdOrThrow(userRepository, (long) userId, UserErrorCode.USER_NOT_FOUND);
        transaction.setStatus(FinanceTransactionStatus.REJECTED);
        transaction.setApprovedBy(admin);
        transaction.setApprovedAt(LocalDateTime.now());
        transaction.setRejectReason(request.getRejectReason());

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    private FinanceTransaction getActiveTransaction(Long id) {
        return transactionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(FinanceTransactionErrorCode.TRANSACTION_NOT_FOUND));
    }

    private Specification<FinanceTransaction> buildSearchSpecification(String keyword, FinanceTransactionType type,
                                                                       FinanceTransactionStatus status,
                                                                       LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.equal(root.get("isActive"), true);

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
