package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.WarehouseResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.exception.WarehouseErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.WarehouseMapper;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import com.overcode250204.smartlogicticssystem.services.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends BaseServiceImpl implements IWarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private void checkAdminRole(int roleId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    @Override
    public List<WarehouseResponseDTO> getAllWarehouses(int roleId, int userId) {
        checkAdminRole(roleId);
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponseDTO getById(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        Warehouse warehouse = findByIdOrThrow(warehouseRepository, id, WarehouseErrorCode.WAREHOUSE_NOT_FOUND); 
        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    public WarehouseResponseDTO create(WarehouseCreateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(savedWarehouse);
    }

    @Override
    public WarehouseResponseDTO update(Long id, WarehouseUpdateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        Warehouse warehouse = findByIdOrThrow(warehouseRepository, id, WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        warehouseMapper.updateEntity(request, warehouse);
        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(updatedWarehouse);
    }

    @Override
    public void delete(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        Warehouse warehouse = findByIdOrThrow(warehouseRepository, id, WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        warehouseRepository.delete(warehouse);
    }
}
