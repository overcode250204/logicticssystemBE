package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ZoneResponseDTO {

    private Long id;

    private String name;

    private Map<String, Object> coverageArea;

    private LocalDateTime createAt;
}
