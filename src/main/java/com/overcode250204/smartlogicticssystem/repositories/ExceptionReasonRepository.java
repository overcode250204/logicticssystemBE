package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.ExceptionReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExceptionReasonRepository extends JpaRepository<ExceptionReason, Long> {
    Optional<ExceptionReason> findByCategoryIgnoreCaseAndReasonTextIgnoreCase(
            String category,
            String reasonText
    );
}
