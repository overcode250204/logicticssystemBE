package com.overcode250204.smartlogicticssystem.services.impls;


import com.overcode250204.smartlogicticssystem.base.BaseService;
import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.location.PointDTO;
import com.overcode250204.smartlogicticssystem.dtos.request.location.ZoneRequestDTO;
import com.overcode250204.smartlogicticssystem.entities.Zone;
import com.overcode250204.smartlogicticssystem.repositories.ZoneRepository;
import com.overcode250204.smartlogicticssystem.services.IZoneService;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
@AllArgsConstructor
public class ZoneService extends BaseServiceImpl implements IZoneService {

    private final ZoneRepository zoneRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


    @Override
    public ZoneRequestDTO create(ZoneRequestDTO dto, int roleId, int userId) {
        Zone zone = Zone.builder()
                .name(dto.getName())
                .polygon(mappToPolygon(dto.getCoordinates()))
                .build();
        zoneRepository.save(zone);
        return  dto;
    }

    @Override
    public ZoneRequestDTO update(Long aLong, ZoneRequestDTO dto, int roleId, int userId) {
        return null;
    }

    @Override
    public ZoneRequestDTO getById(Long aLong, int roleId, int userId) {
        return null;
    }

    @Override
    public void delete(Long aLong, int roleId, int userId) {

    }

    private Point mappToPoint(PointDTO point) {
        return geometryFactory.createPoint(new Coordinate(point.getLongitude(), point.getLatitude()));
    }

    private Polygon mappToPolygon(List<PointDTO> points){
        Coordinate[] coordinates = points.stream().map((x) -> new Coordinate(x.getLongitude(), x.getLatitude())).toArray(Coordinate[]::new);
        // đóng polygon
        coordinates = Arrays.copyOf(coordinates, coordinates.length + 1);
        coordinates[coordinates.length - 1] = coordinates[0];
        return geometryFactory.createPolygon(coordinates);
    }
}
