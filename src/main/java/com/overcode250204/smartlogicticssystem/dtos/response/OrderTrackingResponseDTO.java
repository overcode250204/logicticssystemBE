package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderTrackingResponseDTO {
    private Long trackingId;
    private Long orderId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime recordedAt;
    private String note;
}
