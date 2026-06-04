package com.overcode250204.smartlogicticssystem.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public record InventoryDeductedEvent(Long recipientId,
                                     String staffName,
                                     String productName,
                                     Integer quantity,
                                     LocalDateTime deductedAt){

}
