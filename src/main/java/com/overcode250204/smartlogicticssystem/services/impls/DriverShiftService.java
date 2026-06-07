package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.request.shift.ShiftRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.shift.ShiftUpdateStatusRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.shift.ShiftResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.DriverProfile;
import com.overcode250204.smartlogicticssystem.entities.DriverShift;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.repositories.DriverProfileRepository;
import com.overcode250204.smartlogicticssystem.repositories.DriverShiftRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DriverShiftService {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final DriverShiftRepository driverShiftRepository;


    public ShiftResponseDTO create(ShiftRequest request, int roleId, int userId){
        User user = userRepository.findById((long)userId).orElseThrow(() -> new RuntimeException("User not found"));
        DriverProfile dp = driverProfileRepository.findByUserId((long)userId).orElseThrow(() -> new RuntimeException("Driver not found"));
        DriverShift shift = DriverShift.builder()
                .driverProfile(dp)
                .workStatus("OFF_SHIFT")
                .build();
        shift = driverShiftRepository.save(shift);
        return ShiftResponseDTO.builder().id(shift.getId()).workStatus(shift.getWorkStatus()).build();
    }

    // To check in check out rest
    public ShiftResponseDTO updateWorkStatus(Long id,ShiftUpdateStatusRequest request, int roleId, int userId){
        DriverShift shift = driverShiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
        if(shift.getDriverProfile().getUserId() != (long)userId){
            throw  new RuntimeException("Shift not found");
        }
        shift.setWorkStatus(request.getWorkStatus());
        shift = driverShiftRepository.save(shift);
        return ShiftResponseDTO.builder().id(shift.getId()).workStatus(shift.getWorkStatus()).build();
    }

    public void delete(Long id, int roleId, int userId){
        DriverShift shift = driverShiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
        if(shift.getDriverProfile().getUserId() != (long)userId){
            throw  new RuntimeException("Shift not found");
        }
       driverShiftRepository.delete(shift);
    }

    public ShiftResponseDTO getById(Long id, int roleId, int userId){
        DriverShift shift = driverShiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
        if(shift.getDriverProfile().getUserId() != (long)userId){
            throw  new RuntimeException("Shift not found");
        }
        return ShiftResponseDTO.builder().id(shift.getId()).workStatus(shift.getWorkStatus()).build();
    }

    public List<ShiftResponseDTO> getAll( int roleId, int userId){
        List<ShiftResponseDTO> shifts = driverShiftRepository.getAllByDriverProfile_UserId((long)userId)
                .stream().map((x) -> ShiftResponseDTO.builder().id(x.getId()).workStatus(x.getWorkStatus()).build()).toList();
        if(shifts.isEmpty()){
            throw  new RuntimeException("Shift not found");
        }
        return shifts;
    }
}
