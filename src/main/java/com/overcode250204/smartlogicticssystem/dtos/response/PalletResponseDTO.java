package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PalletResponseDTO {

    private Long palletId;

    private String palletCode;

    private String barcodeUrl;

    private LinehaulTripResponseDTO linehaulTrip;

    private List<PalletItemResponseDTO> orders;

    private PalletStatus status;

    private LocalDateTime createdAt;
}
