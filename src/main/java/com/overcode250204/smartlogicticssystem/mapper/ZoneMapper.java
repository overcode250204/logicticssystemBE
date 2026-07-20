package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.ZoneResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Zone;
import org.springframework.stereotype.Component;
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONWriter;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class ZoneMapper {
    public ZoneResponseDTO toResponse(Zone zone) {
        if (zone == null) {
            return null;
        }
        GeoJSONWriter writer = new GeoJSONWriter();
        GeoJSON geoJson = writer.write(zone.getPolygon());

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> polygon = mapper.convertValue(geoJson, Map.class);

        return ZoneResponseDTO.builder()
                .id(zone.getZoneId())
                .name(zone.getName())
                .coverageArea(polygon)
                .createAt(zone.getCreateAt())
                .slaHours(zone.getSlaHours())
                .build();
    }

//    public Vehicle toEntity(VehicleCreateRequest request) {
//        if (request == null) {
//            return null;
//        }
//
//        Vehicle vehicle = new Vehicle();
//        vehicle.setLicensePlate(request.getLicensePlate());
//        vehicle.setVehicleType(request.getVehicleType());
//        vehicle.setMaxWeightKg(request.getMaxWeightKg());
//        vehicle.setMaxVolumeM3(request.getMaxVolumeM3());
//
//        if (request.getStatus() != null) {
//            vehicle.setStatus(request.getStatus());
//        }
//
//        return vehicle;
//    }
//
//    public void updateEntity(VehicleUpdateRequest request, Vehicle vehicle) {
//        if (request == null || vehicle == null) {
//            return;
//        }
//
//        vehicle.setLicensePlate(request.getLicensePlate());
//        vehicle.setVehicleType(request.getVehicleType());
//        vehicle.setMaxWeightKg(request.getMaxWeightKg());
//        vehicle.setMaxVolumeM3(request.getMaxVolumeM3());
//
//        if (request.getStatus() != null) {
//            vehicle.setStatus(request.getStatus());
//        }
//    }
}
