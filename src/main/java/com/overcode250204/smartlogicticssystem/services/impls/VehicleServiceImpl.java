package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.VehicleCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.VehicleUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.VehicleResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.exception.VehicleErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.VehicleMapper;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.services.IVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl extends BaseServiceImpl implements IVehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    private void checkAdminRole(int roleId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicles(int roleId, int userId) {
        checkAdminRole(roleId);
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDTO getById(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        Vehicle vehicle = findByIdOrThrow(vehicleRepository, id, VehicleErrorCode.VEHICLE_NOT_FOUND); 
        return vehicleMapper.toResponse(vehicle);
    }

    @Override
    public VehicleResponseDTO create(VehicleCreateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new AppException(VehicleErrorCode.LICENSE_PLATE_ALREADY_EXISTS);
        }
        Vehicle vehicle = vehicleMapper.toEntity(request);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Override
    public VehicleResponseDTO update(Long id, VehicleUpdateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        Vehicle vehicle = findByIdOrThrow(vehicleRepository, id, VehicleErrorCode.VEHICLE_NOT_FOUND);
        
        if (!vehicle.getLicensePlate().equals(request.getLicensePlate()) 
            && vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new AppException(VehicleErrorCode.LICENSE_PLATE_ALREADY_EXISTS);
        }

        vehicleMapper.updateEntity(request, vehicle);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(updatedVehicle);
    }

    @Override
    public void delete(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        Vehicle vehicle = findByIdOrThrow(vehicleRepository, id, VehicleErrorCode.VEHICLE_NOT_FOUND);
        vehicleRepository.delete(vehicle);
    }
}
