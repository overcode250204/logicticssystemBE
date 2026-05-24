package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
import com.overcode250204.smartlogicticssystem.dtos.InventoryDTO;
import java.util.List;

public interface IInventoryTransactionService extends BaseService<InventoryTransactionDTO, Long> {
    List<InventoryTransactionDTO> getAllTransactions();

    List<InventoryDTO> getAllTransactionResponses();

}

