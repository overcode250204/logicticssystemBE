package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.request.CustomerAddressRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.CustomerAddressResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.CustomerAddress;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.repositories.CustomerAddressRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.ICustomerAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements ICustomerAddressService {

    private static final int CUSTOMER_ROLE_ID = 5;

    private final CustomerAddressRepository customerAddressRepository;
    private final UserRepository userRepository;

    @Override
    public List<CustomerAddressResponseDTO> getMyAddresses(int roleId, int userId) {
        validateCustomerRole(roleId);
        return customerAddressRepository
                .findByCustomer_UserIdOrderByIsDefaultDescUpdatedAtDescCreatedAtDesc((long) userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CustomerAddressResponseDTO getDefaultAddress(int roleId, int userId) {
        validateCustomerRole(roleId);
        return customerAddressRepository.findFirstByCustomer_UserIdAndIsDefaultTrue((long) userId)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public CustomerAddressResponseDTO create(CustomerAddressRequest request, int roleId, int userId) {
        validateCustomerRole(roleId);
        User customer = userRepository.findById((long) userId)
                .orElseThrow(() -> new AppException(RoleErrorCode.ROLE_ID_NOT_FOUND));

        boolean duplicate = customerAddressRepository
                .existsByCustomer_UserIdAndReceiverNameAndPhoneAndProvinceCodeAndDeliveryAddressAndLatitudeAndLongitude(
                        (long) userId,
                        request.getReceiverName().trim(),
                        request.getPhone().trim(),
                        request.getProvinceCode(),
                        request.getDeliveryAddress().trim(),
                        request.getLatitude(),
                        request.getLongitude()
                );
        if (duplicate) {
            return customerAddressRepository
                    .findByCustomer_UserIdOrderByIsDefaultDescUpdatedAtDescCreatedAtDesc((long) userId)
                    .stream()
                    .filter(address -> sameAddress(address, request))
                    .findFirst()
                    .map(this::toResponse)
                    .orElse(null);
        }

        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        applyRequest(address, request);

        if (Boolean.TRUE.equals(request.getIsDefault())
                || customerAddressRepository.findFirstByCustomer_UserIdAndIsDefaultTrue((long) userId).isEmpty()) {
            customerAddressRepository.clearDefaultByCustomerId((long) userId);
            address.setIsDefault(true);
        }

        return toResponse(customerAddressRepository.save(address));
    }

    @Override
    @Transactional
    public CustomerAddressResponseDTO update(Long id, CustomerAddressRequest request, int roleId, int userId) {
        validateCustomerRole(roleId);
        CustomerAddress address = getOwnedAddress(id, userId);
        applyRequest(address, request);
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            customerAddressRepository.clearDefaultByCustomerId((long) userId);
            address.setIsDefault(true);
        }
        return toResponse(customerAddressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        validateCustomerRole(roleId);
        CustomerAddress address = getOwnedAddress(id, userId);
        customerAddressRepository.delete(address);
    }

    @Override
    @Transactional
    public CustomerAddressResponseDTO setDefault(Long id, int roleId, int userId) {
        validateCustomerRole(roleId);
        CustomerAddress address = getOwnedAddress(id, userId);
        customerAddressRepository.clearDefaultByCustomerId((long) userId);
        address.setIsDefault(true);
        return toResponse(customerAddressRepository.save(address));
    }

    private CustomerAddress getOwnedAddress(Long id, int userId) {
        return customerAddressRepository.findByAddressIdAndCustomer_UserId(id, (long) userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
    }

    private void validateCustomerRole(int roleId) {
        if (roleId != CUSTOMER_ROLE_ID) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    private void applyRequest(CustomerAddress address, CustomerAddressRequest request) {
        address.setReceiverName(request.getReceiverName().trim());
        address.setPhone(request.getPhone().trim());
        address.setProvinceCode(request.getProvinceCode());
        address.setProvinceName(request.getProvinceName().trim());
        address.setDeliveryAddress(request.getDeliveryAddress().trim());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setLabel(request.getLabel() == null || request.getLabel().isBlank() ? "Nhà riêng" : request.getLabel().trim());
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }
    }

    private boolean sameAddress(CustomerAddress address, CustomerAddressRequest request) {
        return address.getReceiverName().equals(request.getReceiverName().trim())
                && address.getPhone().equals(request.getPhone().trim())
                && address.getProvinceCode().equals(request.getProvinceCode())
                && address.getDeliveryAddress().equals(request.getDeliveryAddress().trim())
                && address.getLatitude().compareTo(request.getLatitude()) == 0
                && address.getLongitude().compareTo(request.getLongitude()) == 0;
    }

    private CustomerAddressResponseDTO toResponse(CustomerAddress address) {
        return CustomerAddressResponseDTO.builder()
                .addressId(address.getAddressId())
                .receiverName(address.getReceiverName())
                .phone(address.getPhone())
                .provinceCode(address.getProvinceCode())
                .provinceName(address.getProvinceName())
                .deliveryAddress(address.getDeliveryAddress())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.getIsDefault())
                .label(address.getLabel())
                .build();
    }
}
