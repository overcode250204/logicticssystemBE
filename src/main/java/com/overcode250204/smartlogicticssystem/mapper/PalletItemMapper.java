package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.PalletItemResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.PalletItem;
import org.springframework.stereotype.Component;

@Component
public class PalletItemMapper {
    public PalletItemResponseDTO toResponse(PalletItem entity) {
        if (entity == null) {
            return null;
        }


        return PalletItemResponseDTO.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getOrderId())
                .scannedAt(entity.getScannedAt()).build();
    }
}
