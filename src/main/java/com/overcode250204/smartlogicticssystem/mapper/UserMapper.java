package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setIdentificationNumber(user.getIdentificationNumber());
        dto.setOrigin(user.getOrigin());
        dto.setAddress(user.getAddress());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIsActive(user.getIsActive());

        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getRoleId());
            dto.setRoleName(user.getRole().getRoleName());
        }

        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUserId(dto.getUserId());
        user.setFullName(dto.getFullName());
        user.setIdentificationNumber(dto.getIdentificationNumber());
        user.setOrigin(dto.getOrigin());
        user.setAddress(dto.getAddress());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setIsActive(dto.getIsActive());
        user.setPasswordHash(dto.getPassword());

        return user;
    }
}
