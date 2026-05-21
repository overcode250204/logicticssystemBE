package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.SupplierDTO;
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
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SupplierDTO create(SupplierDTO dto, int roleId, int userId) {
        Supplier supplier = supplierMapper.toEntity(dto);
        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public SupplierDTO update(Integer id, SupplierDTO dto, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, id, SupplierErrorCode.SUPPLIER_NOT_FOUND);
        supplierMapper.updateEntity(dto, supplier);
        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }

    @Override
    public SupplierDTO getById(Integer id, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, id, SupplierErrorCode.SUPPLIER_NOT_FOUND);
        return supplierMapper.toDTO(supplier);
    }

    @Override
    @Transactional
    public void delete(Integer id, int roleId, int userId) {
        Supplier supplier = findByIdOrThrow(supplierRepository, id, SupplierErrorCode.SUPPLIER_NOT_FOUND);
        supplierRepository.delete(supplier);
    }
}
