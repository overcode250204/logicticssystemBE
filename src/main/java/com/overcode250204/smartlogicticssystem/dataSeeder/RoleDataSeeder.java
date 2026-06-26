package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Role;
import com.overcode250204.smartlogicticssystem.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleDataSeeder implements DataSeeder {

    private final RoleRepository roleRepository;

    @Override
    public int getOrder() {
        return 1; // Role chạy trước User
    }

    @Override
    @Transactional
    public void seed() {
        createRoleIfNotExists(
                "ADMIN",
                "Quản trị viên hệ thống"
        );

        createRoleIfNotExists(
                "WAREHOUSE_KEEPER",
                "Thủ kho quản lý hàng hóa trong kho"
        );

        createRoleIfNotExists(
                "DRIVER",
                "Tài xế giao nhận hàng hóa"
        );

        createRoleIfNotExists(
                "STAFF",
                "Nhân viên vận hành hệ thống"
        );

        log.info("Role seed data completed.");
    }

    private void createRoleIfNotExists(
            String roleName,
            String description
    ) {
        boolean exists = roleRepository.existsByRoleName(roleName);

        if (exists) {
            log.info("Role already exists: {}", roleName);
            return;
        }

        Role role = new Role();
        role.setRoleName(roleName);
        role.setDescription(description);

        roleRepository.save(role);

        log.info("Created role: {}", roleName);
    }
}