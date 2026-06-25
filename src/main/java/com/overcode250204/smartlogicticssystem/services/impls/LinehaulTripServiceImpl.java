package com.overcode250204.smartlogicticssystem.services.impls;


import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.DriverErrorCode;
import com.overcode250204.smartlogicticssystem.exception.LinehaulTripErrorCode;
import com.overcode250204.smartlogicticssystem.exception.VehicleErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.LinehaulTripMapper;
import com.overcode250204.smartlogicticssystem.repositories.DriverRepository;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.services.ILinehaulTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class LinehaulTripServiceImpl extends BaseServiceImpl implements ILinehaulTripService {
    private final LinehaulTripRepository linehaulTripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final LinehaulTripMapper linehaulTripMapper;
    private final PalletRepository palletRepository;


    @Override
    public LinehaulTripResponseDTO update(Long id, LinehaulTripUpdateRequest request, int roleId, int userId) {
        // TODO check role is ADMIN -> TO UPDATE VEHICLE and DRIVER
        // TODO because DRIVER only CHANGE STATUS
        //TODO check driver Type because Driver must be LineHaul
        // TODO check vehicle constraint because Vehicle must suitable for TRIP
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);
        List<Pallet> pallets = palletRepository.findPalletByLinehaulTrip(linehaulTrip);
        Driver driver = findByIdOrThrow(driverRepository, request.getDriverId(), DriverErrorCode.DRIVER_NOT_FOUND);
        Vehicle vehicle = findByIdOrThrow(vehicleRepository, request.getVehicleId(), VehicleErrorCode.VEHICLE_NOT_FOUND);
        linehaulTrip.setDriver(driver);
        linehaulTrip.setVehicle(vehicle);
        LinehaulTripStatus newStatus = request.getStatus();

        linehaulTrip.setStatus(newStatus);

        if(LinehaulTripStatus.EN_ROUTE.equals(newStatus)){
            if(pallets.stream().anyMatch(p -> PalletStatus.CREATING.equals(p.getStatus())) || pallets.isEmpty()){
                throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_EN_ROUTE);
            }
            linehaulTrip.setDepartureTime(LocalDateTime.now());

        }
        //TODO check GPS of DRIVER in from Warehouse to can Change STATUS
        if(LinehaulTripStatus.ARRIVED.equals(newStatus)){
            linehaulTrip.setArrivalTime(LocalDateTime.now());
        }
        linehaulTripRepository.save(linehaulTrip);
        return linehaulTripMapper.toResponse(linehaulTrip);
    }
}
