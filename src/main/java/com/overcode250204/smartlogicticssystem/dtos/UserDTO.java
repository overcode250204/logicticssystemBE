package com.overcode250204.smartlogicticssystem.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    @NotBlank(message = "Full name cannot blank")
    @Size(min = 5, max = 100, message = "Full name must be between 5 and 100 character")
    private String fullName;
    @Email
    private String email;
    private String phone;
    private Integer roleId;
    private String roleName;
    private Boolean isActive;
    private String password;
}
