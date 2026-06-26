package com.overcode250204.smartlogicticssystem.dataSeeder;


import com.overcode250204.smartlogicticssystem.entities.Role;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.repositories.RoleRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public int getOrder() {
        return 2; // Chạy sau RoleDataSeeder
    }

    @Override
    @Transactional
    public void seed() {
        createUserIfNotExists(
                "admin@logistics.com",
                "Nguyễn Văn Quản Trị",
                "0901111111",
                "ADMIN"
        );

        createUserIfNotExists(
                "thukho@logistics.com",
                "Trần Thủ Kho",
                "0902222222",
                "WAREHOUSE_KEEPER"
        );

        createUserIfNotExists(
                "taixe1@logistics.com",
                "Lê Văn Tài Xế",
                "0903333333",
                "DRIVER"
        );

        createUserIfNotExists(
                "staff1@logistics.com",
                "Nguyễn Nhân Viên",
                null,
                "STAFF"
        );

        log.info("User seed data completed.");
    }

    private void createUserIfNotExists(
            String email,
            String fullName,
            String phone,
            String roleName
    ) {
        if (userRepository.existsByEmail(email)) {
            log.info("User already exists: {}", email);
            return;
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() ->
                        new IllegalStateException("Role not found: " + roleName)
                );

        User user = new User();

        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);

        // Password gốc để đăng nhập lần đầu: 123456
        user.setPasswordHash("12345");

        // Đổi thành user.setActive(true) nếu entity của bạn đặt field là active
        user.setIsActive(true);

        user.setRole(role);

        userRepository.save(user);

        log.info("Created seed user: {} with role {}", email, roleName);
    }

}
