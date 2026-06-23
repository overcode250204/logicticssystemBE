package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.WarehouseResponseDTO;

import java.util.List;

public interface IWarehouseService {
    List<WarehouseResponseDTO> getAllWarehouses(int roleId, int userId);
    WarehouseResponseDTO getById(Long id, int roleId, int userId);
    WarehouseResponseDTO create(WarehouseCreateRequest request, int roleId, int userId);
    WarehouseResponseDTO update(Long id, WarehouseUpdateRequest request, int roleId, int userId);
    void delete(Long id, int roleId, int userId);
}
