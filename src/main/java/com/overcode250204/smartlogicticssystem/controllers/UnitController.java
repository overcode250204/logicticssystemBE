package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.UnitResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.UnitType;
import com.overcode250204.smartlogicticssystem.services.IUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController extends BaseController {

    private final IUnitService unitService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<UnitResponseDTO>>> getAll(
            @RequestParam(required = false) UnitType type) {
        return success(unitService.getAllUnits(type), "Units retrieved successfully");
    }
}
