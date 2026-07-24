package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.DriverResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.DriverErrorCode;
import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.mapper.DriverMapper;
import com.overcode250204.smartlogicticssystem.repositories.DriverRepository;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripDriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController extends BaseController {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final LinehaulTripDriverRepository linehaulTripDriverRepository;

    @GetMapping
    public ResponseEntity<BaseResponse<List<DriverResponseDTO>>> getAllDrivers(
            @RequestParam(required = false) java.util.List<Long> currentDriverIds) {
        // Tài xế đang gắn với một chuyến linehaul active thì KHÔNG thể phân công
        // sang chuyến khác. Loại họ khỏi dropdown bằng đúng nguồn dữ liệu mà
        // validation tạo/sửa chuyến dùng (bảng LinehaulTripDriver), thay vì chỉ dựa
        // vào cờ denormalized driver.status vốn có thể lệch (seed, luồng quên đồng bộ).
        java.util.Set<Long> busyDriverIds = new java.util.HashSet<>(
                linehaulTripDriverRepository.findDriverIdsWithActiveAssignments(
                        LinehaulTripStatus.ACTIVE_STATUSES));

        List<DriverResponseDTO> response = driverRepository.findAll().stream()
                // Luôn giữ lại tài xế đang thuộc chuyến đang chỉnh sửa (currentDriverIds)
                // để dropdown hiển thị đúng lựa chọn hiện tại khi mở form sửa.
                .filter(d -> (currentDriverIds != null && currentDriverIds.contains(d.getDriverId()))
                        || (d.getStatus() == DriverStatus.AVAILABLE
                            && !busyDriverIds.contains(d.getDriverId())))
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
