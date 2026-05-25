package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.SupplierCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierResponseDTO;

import java.util.List;

public interface ISupplierService {
    SupplierResponseDTO create(SupplierCreateRequest request, int roleId, int userId);

    SupplierResponseDTO update(Integer id, SupplierUpdateRequest request, int roleId, int userId);

    SupplierResponseDTO getById(Integer id, int roleId, int userId);

    void delete(Integer id, int roleId, int userId);

    List<SupplierResponseDTO> getAllSuppliers();
}
