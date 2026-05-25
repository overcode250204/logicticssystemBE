package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryTransactionResponseDTO;
import java.util.List;

public interface IInventoryTransactionService {
    InventoryTransactionResponseDTO create(InventoryTransactionCreateRequest request, int roleId, int userId);

    InventoryTransactionResponseDTO update(Long id, InventoryTransactionUpdateRequest request, int roleId, int userId);

    InventoryTransactionResponseDTO getById(Long id, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    List<InventoryTransactionResponseDTO> getAllTransactions();
}
