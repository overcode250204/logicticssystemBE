package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.PalletCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;

import java.util.List;

public interface IPalletService {

    PalletResponseDTO create(PalletCreateRequest request, int roleId, int userId);

    PalletResponseDTO getById(Long id, int roleId, int userId);

    List<PalletResponseDTO> getAll(int roleId, int userId);

    PalletResponseDTO update(Long id, PalletUpdateRequest request, int roleId, int userId);

    void delete(Long id, int roleId, int userId);
}
