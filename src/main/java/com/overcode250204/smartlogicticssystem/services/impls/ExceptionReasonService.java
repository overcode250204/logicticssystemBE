package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ExceptionReasonResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.ExceptionReason;
import com.overcode250204.smartlogicticssystem.exception.ExceptionReasonErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.ExceptionReasonMapper;
import com.overcode250204.smartlogicticssystem.repositories.ExceptionReasonRepository;
import com.overcode250204.smartlogicticssystem.services.IExceptionReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExceptionReasonService extends BaseServiceImpl implements IExceptionReasonService {

    private final ExceptionReasonRepository exceptionReasonRepository;
    private final ExceptionReasonMapper exceptionReasonMapper;

    @Override
    public List<ExceptionReasonResponseDTO> getAllExceptionReasons() {
        return exceptionReasonRepository.findAll().stream()
                .map(exceptionReasonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExceptionReasonResponseDTO create(ExceptionReasonCreateRequest request, int roleId, int userId) {
        ExceptionReason entity = exceptionReasonMapper.toEntity(request);
        return exceptionReasonMapper.toResponse(exceptionReasonRepository.save(entity));
    }

    @Override
    @Transactional
    public ExceptionReasonResponseDTO update(Long id, ExceptionReasonUpdateRequest request, int roleId, int userId) {
        ExceptionReason entity = findByIdOrThrow(exceptionReasonRepository, id, ExceptionReasonErrorCode.EXCEPTION_REASON_NOT_FOUND);
        exceptionReasonMapper.updateEntity(request, entity);
        return exceptionReasonMapper.toResponse(exceptionReasonRepository.save(entity));
    }

    @Override
    public ExceptionReasonResponseDTO getById(Long id, int roleId, int userId) {
        ExceptionReason entity = findByIdOrThrow(exceptionReasonRepository, id, ExceptionReasonErrorCode.EXCEPTION_REASON_NOT_FOUND);
        return exceptionReasonMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        ExceptionReason entity = findByIdOrThrow(exceptionReasonRepository, id, ExceptionReasonErrorCode.EXCEPTION_REASON_NOT_FOUND);
        exceptionReasonRepository.delete(entity);
    }
}
