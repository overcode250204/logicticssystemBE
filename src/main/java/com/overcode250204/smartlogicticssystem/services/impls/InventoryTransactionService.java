package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.InventoryTransactionUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.InventoryTransactionResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.InventoryTransaction;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.exception.InventoryErrorCode;

import com.overcode250204.smartlogicticssystem.mapper.InventoryTransactionMapper;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.InventoryTransactionRepository;
import com.overcode250204.smartlogicticssystem.services.IInventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryTransactionService extends BaseServiceImpl implements IInventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryTransactionMapper transactionMapper;


    @Override
    @Transactional
    public InventoryTransactionResponseDTO create(InventoryTransactionCreateRequest request, int roleId, int userId) {
        InventoryTransaction transaction = transactionMapper.toEntity(request);

        InventoryBatch batch = findByIdOrThrow(batchRepository, request.getBatchId(), InventoryErrorCode.BATCH_NOT_FOUND);
        transaction.setBatch(batch);

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public InventoryTransactionResponseDTO update(Long id, InventoryTransactionUpdateRequest request, int roleId, int userId) {
        InventoryTransaction transaction = findByIdOrThrow(transactionRepository, id,
                InventoryErrorCode.TRANSACTION_NOT_FOUND);

        transactionMapper.updateEntity(request, transaction);

        InventoryBatch batch = findByIdOrThrow(batchRepository, request.getBatchId(), InventoryErrorCode.BATCH_NOT_FOUND);
        transaction.setBatch(batch);

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public InventoryTransactionResponseDTO getById(Long id, int roleId, int userId) {
        InventoryTransaction transaction = findByIdOrThrow(transactionRepository, id,
                InventoryErrorCode.TRANSACTION_NOT_FOUND);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        InventoryTransaction transaction = findByIdOrThrow(transactionRepository, id,
                InventoryErrorCode.TRANSACTION_NOT_FOUND);
        transactionRepository.delete(transaction);
    }

    @Override
    public List<InventoryTransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

}
