package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PalletResponseDTO {

    private Long palletId;

    private String palletCode;

    private String barcodeUrl;

    private RouteConfigResponseDTO routeConfig;

    private LinehaulTripResponseDTO linehaulTrip;

    private List<PalletItemResponseDTO> palletItems;

    private PalletStatus status;

    private LocalDateTime createdAt;

    private BigDecimal totalWeightKg ;

    private BigDecimal totalVolumeM3;

    private Boolean isCreatedSystem ;
}
