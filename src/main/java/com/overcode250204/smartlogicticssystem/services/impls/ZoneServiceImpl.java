package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.ZoneCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ZoneResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Zone;
import com.overcode250204.smartlogicticssystem.mapper.ZoneMapper;
import com.overcode250204.smartlogicticssystem.repositories.ZoneRepository;
import com.overcode250204.smartlogicticssystem.services.IZoneService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONReader;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl extends BaseServiceImpl implements IZoneService {
    private final ZoneRepository zoneRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final ZoneMapper zoneMapper;
    GeoJSONReader reader = new GeoJSONReader();


    public ZoneResponseDTO create(ZoneCreateRequest request, int roleId, int userId) {
        ObjectMapper mapper = new ObjectMapper();
        String geoJson = mapper.writeValueAsString(request.getCoverageArea());
        Polygon polygon = (Polygon) reader.read(geoJson);
        Zone zone = Zone.builder()
                .name(request.getName())
                .polygon(polygon)
                .createAt(LocalDateTime.now())
                .build();
        zone = zoneRepository.save(zone);

        return zoneMapper.toResponse(zone);
    }


    public ZoneResponseDTO update(Long id, ZoneCreateRequest request, int roleId, int userId) {
        Zone zone = zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone not found"));
        if (request.getName() != null) {
            zone.setName(request.getName());
        }
        ObjectMapper mapper = new ObjectMapper();
        String geoJson = mapper.writeValueAsString(request.getCoverageArea());
        Polygon polygon = (Polygon) reader.read(geoJson);
        zone.setPolygon(polygon);
        zone = zoneRepository.save(zone);

        return zoneMapper.toResponse(zone);
    }


    public ZoneResponseDTO getById(Long id, int roleId, int userId) {
        Zone zone = zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone not found"));
        return zoneMapper.toResponse(zone);
    }
    public List<ZoneResponseDTO> getAll( int roleId, int userId) {
        List<ZoneResponseDTO> zones = zoneRepository.findAll().stream().map(zoneMapper::toResponse).toList();
        return zones;
    }

    public void delete(Long id, int roleId, int userId) {
        Zone zone = zoneRepository.findById(id).orElseThrow(() -> new RuntimeException("Zone not found"));
        zoneRepository.delete(zone);
    }


}
