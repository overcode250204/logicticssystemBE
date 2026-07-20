package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.request.CustomerAddressRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.CustomerAddressResponseDTO;
import com.overcode250204.smartlogicticssystem.services.ICustomerAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-addresses")
@RequiredArgsConstructor
public class CustomerAddressController extends BaseController {

    private final ICustomerAddressService customerAddressService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<CustomerAddressResponseDTO>>> getMyAddresses(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(customerAddressService.getMyAddresses(roleId, userId), "Customer addresses retrieved successfully");
    }

    @GetMapping("/default")
    public ResponseEntity<BaseResponse<CustomerAddressResponseDTO>> getDefaultAddress(
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(customerAddressService.getDefaultAddress(roleId, userId), "Default address retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CustomerAddressResponseDTO>> create(
            @Valid @RequestBody CustomerAddressRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(customerAddressService.create(request, roleId, userId), "Customer address created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CustomerAddressResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerAddressRequest request,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(customerAddressService.update(id, request, roleId, userId), "Customer address updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        customerAddressService.delete(id, roleId, userId);
        return success(null, "Customer address deleted successfully");
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<BaseResponse<CustomerAddressResponseDTO>> setDefault(
            @PathVariable Long id,
            @RequestHeader(name = "X-Role-Id") int roleId,
            @RequestHeader(name = "X-User-Id") int userId) {
        return success(customerAddressService.setDefault(id, roleId, userId), "Default address updated successfully");
    }
}
