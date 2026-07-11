package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.PalletItemResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.PalletItem;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PalletItemMapper {

    private final OrderMapper orderMapper;

    public PalletItemResponseDTO toResponse(PalletItem entity) {
        if (entity == null) {
            return null;
        }

        return PalletItemResponseDTO.builder()
                .id(entity.getId())
                .order(orderMapper.toResponse(entity.getOrder()))
                .scannedAt(entity.getScannedAt()).build();
    }
}
