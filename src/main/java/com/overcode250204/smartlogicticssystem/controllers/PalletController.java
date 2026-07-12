package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IPalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripGpsRequest;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id,
                                                     @RequestHeader(name = "X-Role-Id") int roleId,
                                                     @RequestHeader(name = "X-User-Id") int userId) {
        palletService.delete(id, roleId, userId);
        return success(null, "Pallet deleted successfully");
    }

    @PostMapping("/{palletId}/items")
    public ResponseEntity<BaseResponse<com.overcode250204.smartlogicticssystem.dtos.response.PalletItemResponseDTO>> addPalletItem(
            @PathVariable Long palletId,
            @Valid @RequestBody com.overcode250204.smartlogicticssystem.dtos.request.PalletItemCreateRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.addPalletItem(request, palletId, roleId, userId), "Pallet item added successfully");
    }

    @DeleteMapping("/{palletId}/items/{orderCode}")
    public ResponseEntity<BaseResponse<Void>> removePalletItem(
            @PathVariable Long palletId,
            @PathVariable String orderCode,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        palletService.removePalletItem(palletId, orderCode, roleId, userId);
        return success(null, "Pallet item removed successfully");
    }

    @PostMapping("/{palletCode}/confirm-arrival")
    public ResponseEntity<BaseResponse<PalletResponseDTO>> confirmPalletArrival(
            @PathVariable String palletCode,
            @Valid @RequestBody LinehaulTripGpsRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.confirmPalletArrival(palletCode, request.getLatitude(), request.getLongitude(), roleId, userId), "Pallet arrival confirmed successfully");
    }

    @PostMapping("/orders/{orderCode}/confirm-arrival")
    public ResponseEntity<BaseResponse<Void>> confirmOrderArrival(
            @PathVariable String orderCode,
            @Valid @RequestBody LinehaulTripGpsRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        palletService.confirmOrderArrival(orderCode, request.getLatitude(), request.getLongitude(), roleId, userId);
        return success(null, "Order arrival confirmed successfully");
    }

    @PostMapping("/{palletId}/seal")
    public ResponseEntity<BaseResponse<PalletResponseDTO>> makeSealed(
            @PathVariable Long palletId,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.makeSealed(palletId, roleId, userId), "Pallet sealed successfully");
    }

    @PostMapping("/{palletId}/items/{orderCode}/scan")
    public ResponseEntity<BaseResponse<com.overcode250204.smartlogicticssystem.dtos.response.PalletItemResponseDTO>> scanPalletItem(
            @PathVariable Long palletId,
            @PathVariable String orderCode,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.scanPalletItem(palletId, orderCode, roleId, userId), "Pallet item scanned successfully");
    }

    @PostMapping("/{palletId}/can-seal")
    public ResponseEntity<BaseResponse<PalletResponseDTO>> updateStatusToCanSeal(
            @PathVariable Long palletId,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(palletService.updateStatusToCanSeal(palletId, roleId, userId), "Pallet status updated to CAN_SEAL successfully");
    }
}
