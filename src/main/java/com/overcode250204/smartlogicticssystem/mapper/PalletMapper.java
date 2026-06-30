package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PalletMapper {
    private final LinehaulTripMapper linehaulTripMapper;
    private final RouteConfigMapper routeConfigMapper;
    private final PalletItemMapper palletItemMapper;

    public PalletResponseDTO toResponse(Pallet entity) {
        if (entity == null) {
            return null;
        }

        return PalletResponseDTO.builder()
                .palletId(entity.getPalletId())
                .palletCode(entity.getPalletCode())
                .barcodeUrl(entity.getBarcodeUrl())
                .routeConfig(routeConfigMapper.toResponse(entity.getRouteConfig()))
                .linehaulTrip(linehaulTripMapper.toResponse(entity.getLinehaulTrip()))
                .palletItems(entity.getPalletItems()
                        .stream()
                        .map(palletItemMapper::toResponse).toList())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .totalWeightKg(entity.getTotalWeightKg())
                .totalVolumeM3(entity.getTotalVolumeM3())
                .isCreatedSystem(entity.getIsCreatedSystem())
                .build();
    }
}
