package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;

import java.util.List;

public interface ILinehaulTripService {

    LinehaulTripResponseDTO create(LinehaulTripCreateRequest request, int roleId, int userId);

    LinehaulTripResponseDTO getById(Long id, int roleId, int userId);

    List<LinehaulTripResponseDTO> getAll(com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus status, int roleId, int userId);

    LinehaulTripResponseDTO update(Long id, LinehaulTripUpdateRequest request, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    LinehaulTripResponseDTO addPallet(Long id, com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripAddPalletRequest request, int roleId, int userId);

    LinehaulTripResponseDTO removePallet(Long id, Long palletId, int roleId, int userId);

    LinehaulTripResponseDTO dispatchTrip(Long id, com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripGpsRequest request, int roleId, int userId);

    LinehaulTripResponseDTO finishTrip(Long id, com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripGpsRequest request, int roleId, int userId);

    LinehaulTripResponseDTO updateStatusToCanStart(Long id, int roleId, int userId);
}

