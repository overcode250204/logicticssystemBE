package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;
import com.overcode250204.smartlogicticssystem.services.ILinehaulTripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/linehaul-trip")
@Slf4j
@RequiredArgsConstructor
public class LinehaulTripController extends BaseController {
    private final ILinehaulTripService linehaulTripService;
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<LinehaulTripResponseDTO>> update(@PathVariable Long id,
                                                                        @Valid @RequestBody LinehaulTripUpdateRequest request,
                                                                        @RequestHeader(name = "X-Role-Id") int roleId,
                                                                        @RequestHeader(name = "X-User-Id") int userId) {
        return success(linehaulTripService.update(id, request, roleId, userId), "Linehaul trip updated successfully");
    }
}
