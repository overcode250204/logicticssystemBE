package com.overcode250204.smartlogicticssystem.dtos.request;

import lombok.Builder;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportStockRequest {
    private Long productId;

    private Integer quantity;
}
