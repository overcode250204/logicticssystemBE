package com.overcode250204.smartlogicticssystem.dtos.request;

import lombok.Data;

import java.util.Map;

@Data
public class ZoneCreateRequest {
    private String name;
    private Map<String, Object> coverageArea;
}
