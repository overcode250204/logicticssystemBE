package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserService extends BaseService<UserDTO, Long> {
    List<UserDTO> getAllUserDTO();
}
