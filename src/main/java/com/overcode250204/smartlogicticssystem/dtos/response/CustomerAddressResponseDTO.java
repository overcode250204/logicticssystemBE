package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CustomerAddressResponseDTO {
    private Long addressId;
    private String receiverName;
    private String phone;
    private Integer provinceCode;
    private String provinceName;
    private String deliveryAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isDefault;
    private String label;
}
