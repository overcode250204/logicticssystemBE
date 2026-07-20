package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PalletItemResponseDTO {

    private Long id;

    private OrderResponseDTO order;

    private Boolean isScanned;

    private LocalDateTime scannedAt;
}
