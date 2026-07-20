package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.CustomerAddressRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.CustomerAddressResponseDTO;

import java.util.List;

public interface ICustomerAddressService {
    List<CustomerAddressResponseDTO> getMyAddresses(int roleId, int userId);

    CustomerAddressResponseDTO getDefaultAddress(int roleId, int userId);

    CustomerAddressResponseDTO create(CustomerAddressRequest request, int roleId, int userId);

    CustomerAddressResponseDTO update(Long id, CustomerAddressRequest request, int roleId, int userId);

    void delete(Long id, int roleId, int userId);

    CustomerAddressResponseDTO setDefault(Long id, int roleId, int userId);
}
