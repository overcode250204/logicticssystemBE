package com.overcode250204.smartlogicticssystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginDTO {
    @NonNull
    private String email;
    @NonNull
    private String password;
}
