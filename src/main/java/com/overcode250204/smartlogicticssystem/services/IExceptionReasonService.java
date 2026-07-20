package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ExceptionReasonResponseDTO;

import java.util.List;

public interface IExceptionReasonService {
    ExceptionReasonResponseDTO create(ExceptionReasonCreateRequest request, int roleId, int userId);
    ExceptionReasonResponseDTO update(Long id, ExceptionReasonUpdateRequest request, int roleId, int userId);
    ExceptionReasonResponseDTO getById(Long id, int roleId, int userId);
    void delete(Long id, int roleId, int userId);
    List<ExceptionReasonResponseDTO> getAllExceptionReasons();
}
