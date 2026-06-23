package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseCrudController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.dtos.LoginDTO;
import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.services.impls.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping("/v1/login")
    public ResponseEntity<BaseResponse<UserDTO>> login(@RequestBody @Valid LoginDTO loginDTO) {
        UserDTO response = authService.login(loginDTO.getEmail(), loginDTO.getPassword());
        return success(response, "Login Successful");
    }

    @PostMapping("/register-driver")
    public ResponseEntity<BaseResponse<UserDTO>> registerDriver(@RequestBody @Valid UserDTO userDTO) {
        UserDTO response = authService.registerDriver(userDTO);
        return success(response, "Driver Register Successful");
    }

    @PostMapping("/v2/login")
    public ResponseEntity<BaseResponse<UserDTO>> login(@RequestBody @Valid UserDTO userDTO) {
        UserDTO response = authService.loginWithFirebase(userDTO);
        return ResponseEntity.ok(BaseResponse.success(response, "Login Successful"));
    }



}
