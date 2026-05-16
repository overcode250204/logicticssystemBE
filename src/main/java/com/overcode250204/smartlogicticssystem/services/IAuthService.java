package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.UserDTO;

public interface IAuthService {
    UserDTO login(String email, String password);
}
