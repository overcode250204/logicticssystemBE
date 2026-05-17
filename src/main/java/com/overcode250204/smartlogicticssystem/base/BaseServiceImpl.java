package com.overcode250204.smartlogicticssystem.base;

import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.IErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseServiceImpl {
    protected <T, ID> T findByIdOrThrow(JpaRepository<T, ID> repository, ID id, IErrorCode errorCode) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(errorCode));
    }
}
