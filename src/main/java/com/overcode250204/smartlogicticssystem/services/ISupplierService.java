package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.SupplierDTO;

import java.util.List;

public interface ISupplierService extends BaseService<SupplierDTO, Integer> {
    List<SupplierDTO> getAllSuppliers();
}
