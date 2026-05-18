package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import java.util.List;

public interface IInventoryBatchService extends BaseService<InventoryBatchDTO, Long> {
    List<InventoryBatchDTO> getAll();
}
