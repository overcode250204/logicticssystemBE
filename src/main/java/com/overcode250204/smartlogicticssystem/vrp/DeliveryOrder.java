package com.overcode250204.smartlogicticssystem.vrp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrder {
    private Long orderId;
    private DeliveryLocation location;
    private BigDecimal weightKg;
    private BigDecimal volumeM3;
}
