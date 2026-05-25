package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.exception.SupplierErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierUpdateRequest {
    @NotBlank(message = SupplierErrorCode.Messages.SUPPLIER_NAME_REQUIRED)
    @Size(max = 150, message = SupplierErrorCode.Messages.SUPPLIER_NAME_TOO_LONG)
    private String supplierName;

    @Size(max = 20, message = "Contact phone must not exceed 20 characters")
    private String contactPhone;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}
