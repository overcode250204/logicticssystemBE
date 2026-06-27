package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IPalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pallet")
@Slf4j
@RequiredArgsConstructor
public class PalletController extends BaseController {

    private final IPalletService palletService;

    @PostMapping
    public ResponseEntity<BaseResponse<PalletResponseDTO>> create(@Valid @RequestBody PalletCreateRequest request,
                                                                  @RequestHeader(name = "X-Role-Id") int roleId,
                                                                  @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.create(request, roleId, userId), "Pallet created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PalletResponseDTO>> getById(@PathVariable Long id,
                                                                   @RequestHeader(name = "X-Role-Id") int roleId,
                                                                   @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.getById(id, roleId, userId), "Pallet retrieved successfully");
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<PalletResponseDTO>>> getAll(@RequestHeader(name = "X-Role-Id") int roleId,
                                                                        @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.getAll(roleId, userId), "All pallets retrieved successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PalletResponseDTO>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody PalletUpdateRequest request,
                                                                  @RequestHeader(name = "X-Role-Id") int roleId,
                                                                  @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.update(id, request, roleId, userId), "Pallet updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                     @RequestHeader(name = "X-User-Id") int userId) {
        palletService.delete(id, roleId, userId);
        return success(null, "Pallet deleted successfully");
    }
}
