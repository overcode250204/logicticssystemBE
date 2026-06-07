package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.driverprofile.DriverProfileDTO;
import com.overcode250204.smartlogicticssystem.entities.DriverProfile;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.mapper.DriverProfileMapper;
import com.overcode250204.smartlogicticssystem.repositories.DriverProfileRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.IDriverProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DriverProfileService extends BaseServiceImpl implements IDriverProfileService {

    private final DriverProfileRepository driverProfileRepository;
    private final UserRepository userRepository;
    private final DriverProfileMapper driverProfileMapper;


    @Override
    public DriverProfileDTO create(DriverProfileDTO dto, int roleId, int userId) {
        User user =userRepository.findById((long) userId).orElseThrow(() -> new RuntimeException("User does not exist"));
        dto.setUserId((long) userId);
        DriverProfile profile = driverProfileMapper.toEntity(dto);
        profile.setStatus("ACTIVE");
        profile = driverProfileRepository.save(profile);
        return driverProfileMapper.toResponse(profile);
    }

    @Override
    public DriverProfileDTO update(Long id, DriverProfileDTO dto, int roleId, int userId) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver profile does not exist"));
        driverProfileMapper.updateEntity(dto, profile);
        profile = driverProfileRepository.save(profile);
        return driverProfileMapper.toResponse(profile);
    }

    @Override
    public DriverProfileDTO getById(Long id, int roleId, int userId) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver profile does not exist"));
        return driverProfileMapper.toResponse(profile);
    }

    @Override
    public void delete(Long id, int roleId, int userId) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver profile does not exist"));
        driverProfileRepository.delete(profile);
    }
}
