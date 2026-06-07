package com.overcode250204.smartlogicticssystem.dtos.request.location;

import lombok.Data;

import java.util.List;

@Data
public class ZoneRequestDTO {
    private Long id;
    private String name;
    private List<PointDTO> coordinates;
}

