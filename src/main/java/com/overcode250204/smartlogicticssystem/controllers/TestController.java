package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.routing.domain.RoutePlanSolution;
import com.overcode250204.smartlogicticssystem.services.impls.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController extends BaseController {

    private final DeliveryService service;

    @GetMapping
    public ResponseEntity<BaseResponse<RoutePlanSolution>> executeRouting() {
        return success(service.executeRouting(), "Create successfully");
    }
}
