package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.CustomerAddress;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.repositories.CustomerAddressRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerAddressDataSeeder implements DataSeeder {

    private final CustomerAddressRepository customerAddressRepository;
    private final UserRepository userRepository;

    @Override
    public int getOrder() {
        return 12;
    }

    @Override
    @Transactional
    public void seed() {
        List<AddressSeed> addresses = List.of(
                new AddressSeed("trandinhbao222@gmail.com", "Khach Hang 1", "0905555551", 79,
                        "Ho Chi Minh", "12 Nguyen Hue, District 1", "10.77530000", "106.70370000", true, "Home"),
                new AddressSeed("trandinhbao222@gmail.com", "Khach Hang 1", "0905555551", 79,
                        "Ho Chi Minh", "45 Nguyen Van Linh, District 7", "10.72990000", "106.72160000", false, "Office"),
                new AddressSeed("customer2@logistics.com", "Khach Hang 2", "0905555552", 79,
                        "Ho Chi Minh", "80 Vo Nguyen Giap, Thu Duc", "10.84210000", "106.75820000", true, "Home"),
                new AddressSeed("customer2@logistics.com", "Khach Hang 2", "0905555552", 74,
                        "Binh Duong", "10 Di An Industrial Park, Binh Duong", "10.90640000", "106.76980000", false, "Warehouse"),
                new AddressSeed("customer3@logistics.com", "Khach Hang 3", "0905555553", 75,
                        "Dong Nai", "18 Pham Van Thuan, Bien Hoa", "10.94520000", "106.82450000", true, "Home"),
                new AddressSeed("customer3@logistics.com", "Khach Hang 3", "0905555553", 79,
                        "Ho Chi Minh", "22 Le Loi, District 1", "10.77380000", "106.70090000", false, "Office")
        );

        addresses.forEach(this::createAddressIfNotExists);
        log.info("Customer-address seed data completed.");
    }

    private void createAddressIfNotExists(AddressSeed seed) {
        User customer = userRepository.findByEmail(seed.customerEmail())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + seed.customerEmail()));
        BigDecimal latitude = new BigDecimal(seed.latitude());
        BigDecimal longitude = new BigDecimal(seed.longitude());

        boolean exists = customerAddressRepository
                .existsByCustomer_UserIdAndReceiverNameAndPhoneAndProvinceCodeAndDeliveryAddressAndLatitudeAndLongitude(
                        customer.getUserId(), seed.receiverName(), seed.phone(), seed.provinceCode(),
                        seed.deliveryAddress(), latitude, longitude
                );
        if (exists) {
            return;
        }

        CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        address.setReceiverName(seed.receiverName());
        address.setPhone(seed.phone());
        address.setProvinceCode(seed.provinceCode());
        address.setProvinceName(seed.provinceName());
        address.setDeliveryAddress(seed.deliveryAddress());
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setIsDefault(seed.isDefault());
        address.setLabel(seed.label());
        customerAddressRepository.save(address);
    }

    private record AddressSeed(
            String customerEmail,
            String receiverName,
            String phone,
            Integer provinceCode,
            String provinceName,
            String deliveryAddress,
            String latitude,
            String longitude,
            Boolean isDefault,
            String label
    ) {
    }
}
