package com.overcode250204.smartlogicticssystem.services.impls;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.entities.Role;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.AuthErrorCode;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UserErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.UserMapper;
import com.overcode250204.smartlogicticssystem.repositories.RoleRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService extends BaseServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserDTO login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User u = user.get();
            if(! Boolean.TRUE.equals(u.getIsActive())) {
                throw new AppException(UserErrorCode.USER_IS_INACTIVE);
            }
            if (u.getPasswordHash().equals(password)) {
                return userMapper.toDTO(u);
            }
        }
        return null;
    }

    @Override
    public UserDTO registerDriver(UserDTO userDTO) {
        Role driverRole = findByIdOrThrow(roleRepository, userDTO.getRoleId(), RoleErrorCode.ERROR_DRIVER_ROLE);
        User driverUser = new User();
        driverUser.setFullName(userDTO.getFullName());
        driverUser.setPhone(userDTO.getPhone());
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            driverUser.setEmail(userDTO.getEmail());
        } else {
            String generatedEmail = userDTO.getPhone() + "@driver.com";
            driverUser.setEmail(generatedEmail);
        }
        driverUser.setIdentificationNumber(userDTO.getIdentificationNumber());
        driverUser.setAddress(userDTO.getAddress());
        driverUser.setOrigin(userDTO.getOrigin());
        driverUser.setPasswordHash(userDTO.getPassword());
        driverUser.setRole(driverRole);
        driverUser.setIsActive(Boolean.FALSE); //Waiting for Admin permit
        userRepository.save(driverUser);
        return userMapper.toDTO(driverUser);
    }

    @Override
    public UserDTO loginWithFirebase(UserDTO userDTO) {
        String idToken = userDTO.getToken();
        String phoneNumber = userDTO.getPhone();
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String phone = (String) decodedToken.getClaims().get("phone_number");
            String targetPhone = (phone != null && !phone.isEmpty()) ? phone : phoneNumber;
            String convertPhone = targetPhone.replace("+84", "0");
            Optional<User> localUser = userRepository.findByPhone(convertPhone);
            User user;
            if (localUser.isPresent()) {
                user = localUser.get();
            } else {
                user = new User();
                user.setPhone(targetPhone);
                user.setEmail(targetPhone + "@logistics.com");
                Role driverRole = roleRepository.findById(3)
                        .orElseThrow(() -> new AppException(RoleErrorCode.ROLE_ID_NOT_FOUND));
                user.setRole(driverRole);
                user.setIsActive(false);
                userRepository.save(user);
            }
            userDTO.setUserId(user.getUserId());
            userDTO.setRoleId(user.getRole().getRoleId());
            userDTO.setFullName(user.getFullName());
            userDTO.setIsActive(user.getIsActive());
            return userDTO;
        } catch (Exception e) {
            throw new AppException(AuthErrorCode.FAIL_TO_FIREBASE_AUTHENTICATION);
        }
    }

}
