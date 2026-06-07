package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.request.driverprofile.DriverProfileDTO;
import com.overcode250204.smartlogicticssystem.services.impls.DriverProfileService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class DriverProfileController extends BaseCrudController<DriverProfileDTO, Long> {


    private final DriverProfileService service;

    protected DriverProfileController(DriverProfileService service) {
        super(service);
        this.service = service;
    }

}
