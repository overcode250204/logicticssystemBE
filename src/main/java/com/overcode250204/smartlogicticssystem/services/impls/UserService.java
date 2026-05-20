package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.entities.Role;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UserErrorCode;
import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.repositories.RoleRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.mapper.UserMapper;
import com.overcode250204.smartlogicticssystem.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends BaseServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public List<UserDTO> getAllUserDTO() {
        return userRepository.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> searchWithConditions(String keyword, Integer roleId, Boolean isActive) {
        return userRepository.searchWithConditions(keyword, roleId, isActive).stream().map(userMapper::toDTO).collect(Collectors.toList());
    }



    @Override
    public UserDTO create(UserDTO dto, int roleId, int userId) {
        Role role = findByIdOrThrow(roleRepository, roleId, RoleErrorCode.ROLE_ID_NOT_FOUND);
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }

        User user = userMapper.toEntity(dto);
        user.setRole(role);
        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO update(Long id, UserDTO dto, int roleId, int userId) {
        Role role = findByIdOrThrow(roleRepository, dto.getRoleId(), RoleErrorCode.ROLE_ID_NOT_FOUND);
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        User user = findByIdOrThrow(userRepository, id, UserErrorCode.USER_NOT_FOUND);
        user.setRole(role);
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());

        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO getById(Long id, int roleId, int userId) {
        User user = findByIdOrThrow(userRepository, id, UserErrorCode.USER_NOT_FOUND);
        return userMapper.toDTO(user);
    }

    @Override
    public void delete(Long id, int roleId, int userId) {
        Role role = findByIdOrThrow(roleRepository, roleId, RoleErrorCode.ROLE_ID_NOT_FOUND);
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        User user = findByIdOrThrow(userRepository, id, UserErrorCode.USER_NOT_FOUND);

        user.setIsActive(false);
        userRepository.save(user);
    }
}
