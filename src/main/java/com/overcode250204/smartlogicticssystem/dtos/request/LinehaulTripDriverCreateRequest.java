package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.DriverRole;
import lombok.Data;

@Data
public class LinehaulTripDriverCreateRequest {

    private Long driverId;

    private DriverRole role;
}
