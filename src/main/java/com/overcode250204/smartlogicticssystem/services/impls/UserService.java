package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.entities.Role;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.exception.UserErrorCode;
import com.overcode250204.smartlogicticssystem.dtos.UserDTO;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.repositories.RoleRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.mapper.UserMapper;
import com.overcode250204.smartlogicticssystem.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends BaseServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final com.overcode250204.smartlogicticssystem.repositories.DriverRepository driverRepository;
    private final com.overcode250204.smartlogicticssystem.repositories.ZoneRepository zoneRepository;
    private final com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository warehouseRepository;
    private final com.overcode250204.smartlogicticssystem.repositories.VehicleRepository vehicleRepository;

    private UserDTO toUserDTOWithDriver(User user) {
        UserDTO dto = userMapper.toDTO(user);
        if (user.getRole() != null && user.getRole().getRoleId() == 3) {
            driverRepository.findByUser_UserId(user.getUserId()).ifPresent(driver -> {
                if (driver.getDriverType() != null) {
                    dto.setDriverType(driver.getDriverType().name());
                }
                if (driver.getZone() != null) {
                    dto.setZoneId(driver.getZone().getZoneId());
                }
                if (driver.getCurrentWarehouse() != null) {
                    dto.setCurrentWarehouseId(driver.getCurrentWarehouse().getWarehouseId());
                }
                if (driver.getCurrentVehicle() != null) {
                    dto.setCurrentVehicleId(driver.getCurrentVehicle().getVehicleId());
                }
            });
        }
        return dto;
    }

    @Override
    public List<UserDTO> getAllUserDTO() {
        return userRepository.findAll().stream().map(this::toUserDTOWithDriver).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> searchWithConditions(String keyword, Integer roleId, Boolean isActive) {
        return userRepository.searchWithConditions(keyword, roleId, isActive).stream().map(this::toUserDTOWithDriver).collect(Collectors.toList());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserDTO create(UserDTO dto, int roleId, int userId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        Role role = findByIdOrThrow(roleRepository, dto.getRoleId(), RoleErrorCode.ROLE_ID_NOT_FOUND);

        User user = userMapper.toEntity(dto);
        user.setRole(role);
        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        User savedUser = userRepository.save(user);

        if (role.getRoleId() == 3) {
            com.overcode250204.smartlogicticssystem.entities.Driver driver = new com.overcode250204.smartlogicticssystem.entities.Driver();
            driver.setUser(savedUser);
            driver.setName(savedUser.getFullName());
            driver.setPhone(savedUser.getPhone());
            driver.setStatus(com.overcode250204.smartlogicticssystem.enums.DriverStatus.AVAILABLE);
            if (dto.getDriverType() != null && !dto.getDriverType().isEmpty()) {
                driver.setDriverType(com.overcode250204.smartlogicticssystem.enums.DriverType.valueOf(dto.getDriverType()));
            } else {
                driver.setDriverType(com.overcode250204.smartlogicticssystem.enums.DriverType.LAST_MILE);
            }
            if (dto.getZoneId() != null) {
                com.overcode250204.smartlogicticssystem.entities.Zone zone = zoneRepository.findById(dto.getZoneId())
                        .orElseThrow(() -> new AppException(com.overcode250204.smartlogicticssystem.exception.AppErrorCode.INVALID_KEY, "Zone not found"));
                driver.setZone(zone);
            }
            if (dto.getCurrentWarehouseId() != null) {
                com.overcode250204.smartlogicticssystem.entities.Warehouse warehouse = warehouseRepository.findById(dto.getCurrentWarehouseId())
                        .orElseThrow(() -> new AppException(com.overcode250204.smartlogicticssystem.exception.AppErrorCode.INVALID_KEY, "Warehouse not found"));
                driver.setCurrentWarehouse(warehouse);
            }
            if (dto.getCurrentVehicleId() != null) {
                com.overcode250204.smartlogicticssystem.entities.Vehicle vehicle = vehicleRepository.findById(dto.getCurrentVehicleId())
                        .orElseThrow(() -> new AppException(com.overcode250204.smartlogicticssystem.exception.AppErrorCode.INVALID_KEY, "Vehicle not found"));
                driver.setCurrentVehicle(vehicle);
            }
            driverRepository.save(driver);
        }

        return toUserDTOWithDriver(savedUser);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserDTO update(Long id, UserDTO dto, int roleId, int userId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        Role role = findByIdOrThrow(roleRepository, dto.getRoleId(), RoleErrorCode.ROLE_ID_NOT_FOUND);
        User user = findByIdOrThrow(userRepository, id, UserErrorCode.USER_NOT_FOUND);
        user.setRole(role);
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());

        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }

        User updatedUser = userRepository.save(user);

        if (role.getRoleId() == 3) {
            com.overcode250204.smartlogicticssystem.entities.Driver driver = driverRepository.findByUser_UserId(user.getUserId())
                    .orElseGet(() -> {
                        com.overcode250204.smartlogicticssystem.entities.Driver d = new com.overcode250204.smartlogicticssystem.entities.Driver();
                        d.setUser(updatedUser);
                        d.setStatus(com.overcode250204.smartlogicticssystem.enums.DriverStatus.AVAILABLE);
                        return d;
                    });
            driver.setName(updatedUser.getFullName());
            driver.setPhone(updatedUser.getPhone());
            if (dto.getDriverType() != null && !dto.getDriverType().isEmpty()) {
                driver.setDriverType(com.overcode250204.smartlogicticssystem.enums.DriverType.valueOf(dto.getDriverType()));
            }
            if (dto.getZoneId() != null) {
                com.overcode250204.smartlogicticssystem.entities.Zone zone = zoneRepository.findById(dto.getZoneId())
                        .orElseThrow(() -> new AppException(com.overcode250204.smartlogicticssystem.exception.AppErrorCode.INVALID_KEY, "Zone not found"));
                driver.setZone(zone);
            } else {
                driver.setZone(null);
            }
            if (dto.getCurrentWarehouseId() != null) {
                com.overcode250204.smartlogicticssystem.entities.Warehouse warehouse = warehouseRepository.findById(dto.getCurrentWarehouseId())
                        .orElseThrow(() -> new AppException(com.overcode250204.smartlogicticssystem.exception.AppErrorCode.INVALID_KEY, "Warehouse not found"));
                driver.setCurrentWarehouse(warehouse);
            } else {
                driver.setCurrentWarehouse(null);
            }
            if (dto.getCurrentVehicleId() != null) {
                com.overcode250204.smartlogicticssystem.entities.Vehicle vehicle = vehicleRepository.findById(dto.getCurrentVehicleId())
                        .orElseThrow(() -> new AppException(com.overcode250204.smartlogicticssystem.exception.AppErrorCode.INVALID_KEY, "Vehicle not found"));
                driver.setCurrentVehicle(vehicle);
            } else {
                driver.setCurrentVehicle(null);
            }
            driverRepository.save(driver);
        } else {
            driverRepository.findByUser_UserId(user.getUserId()).ifPresent(driverRepository::delete);
        }

        return toUserDTOWithDriver(updatedUser);
    }

    @Override
    public UserDTO getById(Long id, int roleId, int userId) {
        User user = findByIdOrThrow(userRepository, id, UserErrorCode.USER_NOT_FOUND);
        return toUserDTOWithDriver(user);
    }

    @Override
    public void delete(Long id, int roleId, int userId) {
        Role role = findByIdOrThrow(roleRepository, roleId, RoleErrorCode.ROLE_ID_NOT_FOUND);
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        User user = findByIdOrThrow(userRepository, id, UserErrorCode.USER_NOT_FOUND);

        user.setIsActive(false);
        userRepository.save(user);
    }
}
