package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.PalletItemResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PalletMapper {
    private final LinehaulTripMapper linehaulTripMapper;

    public PalletResponseDTO toResponse(Pallet entity) {
        if (entity == null) {
            return null;
        }

        return PalletResponseDTO.builder()
                .palletId(entity.getPalletId())
                .palletCode(entity.getPalletCode())
                .barcodeUrl(entity.getBarcodeUrl())
                .linehaulTrip(linehaulTripMapper.toResponse(entity.getLinehaulTrip()))
                .orders(entity.getOrders()
                        .stream()
                        .map(x -> PalletItemResponseDTO.builder()
                                .orderId(x.getOrderId())
                                .build()).toList()).status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
