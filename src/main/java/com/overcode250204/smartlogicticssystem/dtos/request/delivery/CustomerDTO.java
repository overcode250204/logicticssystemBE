package com.overcode250204.smartlogicticssystem.dtos.request.delivery;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CustomerDTO {
    private String fullName;
}
