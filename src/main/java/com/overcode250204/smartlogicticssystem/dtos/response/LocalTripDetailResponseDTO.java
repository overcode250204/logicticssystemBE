package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.LocalTripDetailStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalTripDetailResponseDTO {
    private Long id;
    private OrderResponseDTO order;
    private Integer stopOrder;
    private String proofUrl;
    private Boolean barcodeScanned;
    private LocalTripDetailStatus status;
}
