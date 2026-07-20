package com.overcode250204.smartlogicticssystem.repositories;

import com.overcode250204.smartlogicticssystem.entities.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    List<CustomerAddress> findByCustomer_UserIdOrderByIsDefaultDescUpdatedAtDescCreatedAtDesc(Long customerId);

    Optional<CustomerAddress> findByAddressIdAndCustomer_UserId(Long addressId, Long customerId);

    Optional<CustomerAddress> findFirstByCustomer_UserIdAndIsDefaultTrue(Long customerId);

    boolean existsByCustomer_UserIdAndReceiverNameAndPhoneAndProvinceCodeAndDeliveryAddressAndLatitudeAndLongitude(
            Long customerId,
            String receiverName,
            String phone,
            Integer provinceCode,
            String deliveryAddress,
            java.math.BigDecimal latitude,
            java.math.BigDecimal longitude
    );

    @Modifying
    @Query("update CustomerAddress a set a.isDefault = false where a.customer.userId = :customerId")
    void clearDefaultByCustomerId(@Param("customerId") Long customerId);
}
