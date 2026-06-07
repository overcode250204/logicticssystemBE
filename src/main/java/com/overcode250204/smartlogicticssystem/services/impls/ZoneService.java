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
        zone = zoneRepository.save(zone);
        dto.setId(zone.getZoneId());
        return dto;
    }

    @Override
    public ZoneRequestDTO update(Long id, ZoneRequestDTO dto, int roleId, int userId) {
        Zone zone = zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone not found"));
        if (dto.getName() != null) {
            zone.setName(dto.getName());
        }
        if (dto.getCoordinates() != null && !dto.getCoordinates().isEmpty()) {
            zone.setPolygon(mappToPolygon(dto.getCoordinates()));
        }
        zone = zoneRepository.save(zone);
        dto.setId(zone.getZoneId());
        return dto;
    }

    @Override
    public ZoneRequestDTO getById(Long id, int roleId, int userId) {
        Zone zone = zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone not found"));
        ZoneRequestDTO dto = new ZoneRequestDTO();
        dto.setId(zone.getZoneId());
        dto.setName(zone.getName());
        if (zone.getPolygon() != null) {
            dto.setCoordinates(mappToPointDTOs(zone.getPolygon()));
        }
        return dto;
    }

    @Override
    public void delete(Long id, int roleId, int userId) {
        Zone zone = zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone not found"));
        zoneRepository.delete(zone);
    }

    private Point mappToPoint(PointDTO point) {
        return geometryFactory.createPoint(new Coordinate(point.getLongitude(), point.getLatitude()));
    }

    private Polygon mappToPolygon(List<PointDTO> points) {
        Coordinate[] coordinates = points.stream().map((x) -> new Coordinate(x.getLongitude(), x.getLatitude()))
                .toArray(Coordinate[]::new);
        // đóng polygon
        coordinates = Arrays.copyOf(coordinates, coordinates.length + 1);
        coordinates[coordinates.length - 1] = coordinates[0];
        return geometryFactory.createPolygon(coordinates);
    }

    private List<PointDTO> mappToPointDTOs(Polygon polygon) {
        if (polygon == null)
            return null;
        return Arrays.stream(polygon.getCoordinates()).map(coord -> {
            PointDTO p = new PointDTO();
            p.setLongitude(coord.x);
            p.setLatitude(coord.y);
            return p;
        }).toList();
    }
}
