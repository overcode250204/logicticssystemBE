package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.SupplierUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.SupplierResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Supplier;
import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.SupplierMapper;
import com.overcode250204.smartlogicticssystem.repositories.SupplierRepository;
import com.overcode250204.smartlogicticssystem.services.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService extends BaseServiceImpl implements ISupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierResponseDTO create(SupplierCreateRequest request, int roleId, int userId) {
        Supplier supplier = supplierMapper.toEntity(request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public SupplierResponseDTO update(Integer id, SupplierUpdateRequest request, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, id, SupplierErrorCode.SUPPLIER_NOT_FOUND);
        supplierMapper.updateEntity(request, supplier);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Override
    public SupplierResponseDTO getById(Integer id, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, id, SupplierErrorCode.SUPPLIER_NOT_FOUND);
        return supplierMapper.toResponse(supplier);
    }

    @Override
    @Transactional
    public void delete(Integer id, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, id, SupplierErrorCode.SUPPLIER_NOT_FOUND);
        supplierRepository.delete(supplier);
    }
}
