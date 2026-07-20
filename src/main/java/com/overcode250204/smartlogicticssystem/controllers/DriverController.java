package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.DriverResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.DriverErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.DriverMapper;
import com.overcode250204.smartlogicticssystem.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController extends BaseController {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @GetMapping
    public ResponseEntity<BaseResponse<List<DriverResponseDTO>>> getAllDrivers() {
        List<DriverResponseDTO> response = driverRepository.findAll().stream()
                .map(driverMapper::toResponse)
                .collect(Collectors.toList());
        return success(response, "Get all drivers successfully");
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<BaseResponse<DriverResponseDTO>> getDriverByUserId(@PathVariable Long userId) {
        Driver driver = driverRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new AppException(DriverErrorCode.DRIVER_NOT_FOUND));
        return success(driverMapper.toResponse(driver), "Driver retrieved successfully");
    }
}
