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

    private static final String DEFAULT_PASSWORD = "12345";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    @Transactional
    public void seed() {
        createUserIfNotExists("admin@logistics.com", "Nguyen Van Quan Tri", "0901111111", "ADMIN");
        createUserIfNotExists("thukho@logistics.com", "Tran Thu Kho", "0902222222", "WAREHOUSE_KEEPER");

        createUserIfNotExists("taixe1@logistics.com", "Le Van Tai Xe 1", "0903333333", "DRIVER");
        createUserIfNotExists("taixe2@logistics.com", "Tran Van Tai Xe 2", "0903333334", "DRIVER");
        createUserIfNotExists("taixe3@logistics.com", "Nguyen Van Tai Xe 3", "0903333335", "DRIVER");
        createUserIfNotExists("taixe4@logistics.com", "Pham Van Tai Xe 4", "0903333336", "DRIVER");
        createUserIfNotExists("taixe5@logistics.com", "Hoang Van Tai Xe 5", "0903333337", "DRIVER");
        createUserIfNotExists("taixe6@logistics.com", "Vo Van Tai Xe 6", "0903333338", "DRIVER");

        createUserIfNotExists("staff1@logistics.com", "Nguyen Nhan Vien 1", "0904444441", "STAFF");
        createUserIfNotExists("staff2@logistics.com", "Tran Nhan Vien 2", "0904444442", "STAFF");

        createUserIfNotExists("trandinhbao222@gmail.com", "Khach Hang 1", "0905555551", "CUSTOMER");
        createUserIfNotExists("customer2@logistics.com", "Khach Hang 2", "0905555552", "CUSTOMER");
        createUserIfNotExists("customer3@logistics.com", "Khach Hang 3", "0905555553", "CUSTOMER");

        log.info("User seed data completed.");
    }

    private void createUserIfNotExists(String email, String fullName, String phone, String roleName) {
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            boolean changed = false;
            if (existingUser.getPhone() == null || existingUser.getPhone().isBlank()) {
                existingUser.setPhone(phone);
                changed = true;
            }
            if (existingUser.getFullName() == null || existingUser.getFullName().isBlank()) {
                existingUser.setFullName(fullName);
                changed = true;
            }
            if (existingUser.getPasswordHash() == null || existingUser.getPasswordHash().isBlank()) {
                existingUser.setPasswordHash(DEFAULT_PASSWORD);
                changed = true;
            }
            if (changed) {
                userRepository.save(existingUser);
            }
            log.info("User already exists: {}", email);
            return;
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        // Authentication currently compares passwords as plain text.
        user.setPasswordHash(DEFAULT_PASSWORD);
        user.setIsActive(true);
        user.setRole(role);

        userRepository.save(user);
        log.info("Created seed user: {} with role {}", email, roleName);
    }
}
