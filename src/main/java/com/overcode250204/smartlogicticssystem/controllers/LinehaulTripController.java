package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;
import com.overcode250204.smartlogicticssystem.services.ILinehaulTripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/linehaul-trip")
@Slf4j
@RequiredArgsConstructor
public class LinehaulTripController extends BaseController {
    private final ILinehaulTripService linehaulTripService;

    @PostMapping
    public ResponseEntity<BaseResponse<LinehaulTripResponseDTO>> create(@Valid @RequestBody LinehaulTripCreateRequest request,
                                                                        @RequestHeader(name = "X-Role-Id") int roleId,
                                                                        @RequestHeader(name = "X-User-Id") int userId) {
        return success(linehaulTripService.create(request, roleId, userId), "Linehaul trip created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LinehaulTripResponseDTO>> getById(@PathVariable Long id,
                                                                        @RequestHeader(name = "X-Role-Id") int roleId,
                                                                        @RequestHeader(name = "X-User-Id") int userId) {
        return success(linehaulTripService.getById(id, roleId, userId), "Linehaul trip retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<LinehaulTripResponseDTO>>> getAll(@RequestHeader(name = "X-Role-Id") int roleId,
                                                                              @RequestHeader(name = "X-User-Id") int userId) {
        return success(linehaulTripService.getAll(roleId, userId), "All linehaul trips retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<LinehaulTripResponseDTO>> update(@PathVariable Long id,
                                                                        @Valid @RequestBody LinehaulTripUpdateRequest request,
                                                                        @RequestHeader(name = "X-Role-Id") int roleId,
                                                                        @RequestHeader(name = "X-User-Id") int userId) {
        return success(linehaulTripService.update(id, request, roleId, userId), "Linehaul trip updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                     @RequestHeader(name = "X-User-Id") int userId) {
        linehaulTripService.delete(id, roleId, userId);
        return success(null, "Linehaul trip deleted successfully");
    }
}

