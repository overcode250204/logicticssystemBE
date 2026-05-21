package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.InventoryTransactionDTO;
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

@Service
@RequiredArgsConstructor
public class InventoryTransactionService extends BaseServiceImpl implements IInventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryTransactionMapper transactionMapper;

    @Override
    @Transactional
    public InventoryTransactionDTO create(InventoryTransactionDTO dto, int roleId, int userId) {
        InventoryTransaction transaction = transactionMapper.toEntity(dto);

        InventoryBatch batch = findByIdOrThrow(batchRepository, dto.getBatchId(), InventoryErrorCode.BATCH_NOT_FOUND);
        transaction.setBatch(batch);

        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public InventoryTransactionDTO update(Long id, InventoryTransactionDTO dto, int roleId, int userId) {
        InventoryTransaction transaction = findByIdOrThrow(transactionRepository, id,
                InventoryErrorCode.TRANSACTION_NOT_FOUND);

        transaction.setQuantity(dto.getQuantity());
        transaction.setType(dto.getType());

        InventoryBatch batch = findByIdOrThrow(batchRepository, dto.getBatchId(), InventoryErrorCode.BATCH_NOT_FOUND);
        transaction.setBatch(batch);

        return transactionMapper.toDTO(transactionRepository.save(transaction));
    }

    @Override
    public InventoryTransactionDTO getById(Long id, int roleId, int userId) {
        InventoryTransaction transaction = findByIdOrThrow(transactionRepository, id,
                InventoryErrorCode.TRANSACTION_NOT_FOUND);
        return transactionMapper.toDTO(transaction);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        InventoryTransaction transaction = findByIdOrThrow(transactionRepository, id,
                InventoryErrorCode.TRANSACTION_NOT_FOUND);
        transactionRepository.delete(transaction);
    }
}
