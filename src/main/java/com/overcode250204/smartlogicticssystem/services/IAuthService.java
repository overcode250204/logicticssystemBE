package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.UserDTO;

import java.util.Map;

public interface IAuthService {
    UserDTO login(String email, String password);
    UserDTO registerDriver(UserDTO userDTO);
    UserDTO loginWithFirebase(UserDTO userDTO);
}
