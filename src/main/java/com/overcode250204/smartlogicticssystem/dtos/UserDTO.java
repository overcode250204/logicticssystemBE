package com.overcode250204.smartlogicticssystem.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String roleName;
    private Boolean isActive;
    private String password;
}
