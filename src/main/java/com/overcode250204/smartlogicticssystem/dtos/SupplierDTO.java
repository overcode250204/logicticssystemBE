package com.overcode250204.smartlogicticssystem.dtos;

import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDTO {
    private Integer supplierId;

    @NotBlank(message = SupplierErrorCode.Messages.SUPPLIER_NAME_REQUIRED)
    @Size(max = 150, message = SupplierErrorCode.Messages.SUPPLIER_NAME_TOO_LONG)
    private String supplierName;

    private String contactPhone;

    private String address;

    private LocalDateTime createdAt;
}
