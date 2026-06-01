package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.FinanceTransaction;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionStatus;
import com.overcode250204.smartlogicticssystem.enums.FinanceTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Long>, JpaSpecificationExecutor<FinanceTransaction> {
    boolean existsByCode(String code);

    Optional<FinanceTransaction> findByIdAndIsActiveTrue(Long id);

    @Query("""
            SELECT transaction FROM FinanceTransaction transaction
            WHERE transaction.isActive = true
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(transaction.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(transaction.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:type IS NULL OR transaction.type = :type)
              AND (:status IS NULL OR transaction.status = :status)
              AND (:fromDate IS NULL OR transaction.transactionDate >= :fromDate)
              AND (:toDate IS NULL OR transaction.transactionDate <= :toDate)
            """)
    Page<FinanceTransaction> search(
            @Param("keyword") String keyword,
            @Param("type") FinanceTransactionType type,
            @Param("status") FinanceTransactionStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(transaction.amount), 0)
            FROM FinanceTransaction transaction
            WHERE transaction.isActive = true
              AND transaction.status = :status
              AND transaction.type = :type
            """)
    BigDecimal sumAmountByTypeAndStatus(
            @Param("type") FinanceTransactionType type,
            @Param("status") FinanceTransactionStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(transaction.amount), 0)
            FROM FinanceTransaction transaction
            WHERE transaction.isActive = true
              AND transaction.status = :status
              AND transaction.type = :type
              AND transaction.transactionDate >= :fromDate
              AND transaction.transactionDate <= :toDate
            """)
    BigDecimal sumAmountByTypeAndStatusAndDateRange(
            @Param("type") FinanceTransactionType type,
            @Param("status") FinanceTransactionStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    long countByTypeAndStatusAndIsActiveTrue(FinanceTransactionType type, FinanceTransactionStatus status);

    @Query("""
            SELECT COUNT(transaction)
            FROM FinanceTransaction transaction
            WHERE transaction.isActive = true
              AND transaction.status = :status
              AND transaction.type = :type
              AND transaction.transactionDate >= :fromDate
              AND transaction.transactionDate <= :toDate
            """)
    long countByTypeAndStatusAndDateRange(
            @Param("type") FinanceTransactionType type,
            @Param("status") FinanceTransactionStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
