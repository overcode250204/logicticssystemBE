package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.VehicleCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.VehicleUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.VehicleResponseDTO;

import java.util.List;

public interface IVehicleService {
    List<VehicleResponseDTO> getAllVehicles(Long currentVehicleId, int roleId, int userId);
    VehicleResponseDTO getById(Long id, int roleId, int userId);
    VehicleResponseDTO create(VehicleCreateRequest request, int roleId, int userId);
    VehicleResponseDTO update(Long id, VehicleUpdateRequest request, int roleId, int userId);
    void delete(Long id, int roleId, int userId);
}
