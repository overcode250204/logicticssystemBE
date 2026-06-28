package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;

import java.util.List;

public interface ILinehaulTripService {

    LinehaulTripResponseDTO create(LinehaulTripCreateRequest request, int roleId, int userId);

    LinehaulTripResponseDTO getById(Long id, int roleId, int userId);

    List<LinehaulTripResponseDTO> getAll(int roleId, int userId);

    LinehaulTripResponseDTO update(Long id, LinehaulTripUpdateRequest request, int roleId, int userId);

    void delete(Long id, int roleId, int userId);
}

