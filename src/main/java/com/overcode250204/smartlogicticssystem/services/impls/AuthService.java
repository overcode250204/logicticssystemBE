package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.entities.Role;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.UserMapper;
import com.overcode250204.smartlogicticssystem.repositories.RoleRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService extends BaseServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserDTO login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User u = user.get();
            if (u.getPasswordHash().equals(password) && Boolean.TRUE.equals(u.getIsActive())) {
                return userMapper.toDTO(u);
            }
        }
        return null;
    }

    @Override
    public UserDTO registerDriver(UserDTO userDTO) {
        Role driverRole = findByIdOrThrow(roleRepository, userDTO.getRoleId(), RoleErrorCode.ERROR_DRIVER_ROLE);
        User driverUser = new User();
        driverUser.setPhone(userDTO.getFullName());
        driverUser.setPhone(userDTO.getPhone());
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            driverUser.setEmail(userDTO.getEmail());
        } else {
            String generatedEmail = userDTO.getFullName() + userDTO.getPhone() + "@driver.com";
            driverUser.setEmail(generatedEmail);
        }

        driverUser.setPasswordHash(userDTO.getPassword());
        driverUser.setRole(driverRole);
        driverUser.setIsActive(Boolean.FALSE); //Waiting for Admin permit
        userRepository.save(driverUser);
        return userMapper.toDTO(driverUser);
    }

}
