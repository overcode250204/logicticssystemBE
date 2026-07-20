package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.LocalTripDetailResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.LocalTripResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.LocalTrip;
import com.overcode250204.smartlogicticssystem.entities.LocalTripDetail;
import com.overcode250204.smartlogicticssystem.repositories.LocalTripDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LocalTripMapper {

    private final WarehouseMapper warehouseMapper;
    private final DriverMapper driverMapper;
    private final VehicleMapper vehicleMapper;
    private final OrderMapper orderMapper;
    private final LocalTripDetailRepository localTripDetailRepository;

    public LocalTripResponseDTO toResponseDTO(LocalTrip localTrip) {
        if (localTrip == null) {
            return null;
        }
        
        List<LocalTripDetail> details = localTripDetailRepository.findByLocalTrip_LocalTripId(localTrip.getLocalTripId());

        return LocalTripResponseDTO.builder()
                .localTripId(localTrip.getLocalTripId())
                .hub(warehouseMapper.toResponse(localTrip.getHub()))
                .driver(driverMapper.toResponse(localTrip.getDriver()))
                .vehicle(vehicleMapper.toResponse(localTrip.getVehicle()))
                .status(localTrip.getStatus())
                .createdAt(localTrip.getCreatedAt())
                .details(details != null ? details.stream().map(this::toDetailResponseDTO).collect(Collectors.toList()) : null)
                .localTripCode(localTrip.getLocalTripCode())
                .vrpEstimatedMinutes(localTrip.getVrpEstimatedMinutes())
                .build();
    }

    public LocalTripDetailResponseDTO toDetailResponseDTO(LocalTripDetail localTripDetail) {
        if (localTripDetail == null) {
            return null;
        }

        return LocalTripDetailResponseDTO.builder()
                .id(localTripDetail.getId())
                .order(orderMapper.toResponse(localTripDetail.getOrder()))
                .stopOrder(localTripDetail.getStopOrder())
                .proofUrl(localTripDetail.getProofUrl())
                .barcodeScanned(localTripDetail.getBarcodeScanned())
                .status(localTripDetail.getStatus())
                .localTripDetailCode(localTripDetail.getLocalTripDetailCode())
                .build();
    }
}
