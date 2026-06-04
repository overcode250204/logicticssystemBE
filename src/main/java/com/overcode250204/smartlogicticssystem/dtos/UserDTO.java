package com.overcode250204.smartlogicticssystem.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String fullName;
    private String identificationNumber;
    private String origin;
    private String address;
    @Email
    private String email;
    private String phone;
    private Integer roleId;
    private String roleName;
    private Boolean isActive;
    private String password;
    private String token;
}
