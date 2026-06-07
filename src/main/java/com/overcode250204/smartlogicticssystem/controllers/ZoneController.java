package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.dtos.request.location.ZoneRequestDTO;
import com.overcode250204.smartlogicticssystem.services.IZoneService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zones")
public class ZoneController extends BaseCrudController<ZoneRequestDTO, Long> {
    private final IZoneService zoneService;

    protected ZoneController(IZoneService service) {
        super(service);
        this.zoneService = service;
    }
}
