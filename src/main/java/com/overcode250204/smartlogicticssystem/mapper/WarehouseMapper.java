package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.WarehouseUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.WarehouseResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    private final GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);

    public WarehouseResponseDTO toResponse(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }

        Double lat = null;
        Double lon = null;
        if (warehouse.getLocation() != null) {
            lat = warehouse.getLocation().getY();
            lon = warehouse.getLocation().getX();
        }

        return WarehouseResponseDTO.builder()
                .warehouseId(warehouse.getWarehouseId())
                .name(warehouse.getName())
                .type(warehouse.getType())
                .address(warehouse.getAddress())
                .province(warehouse.getProvince())
                .latitude(lat)
                .longitude(lon)
                .createdAt(warehouse.getCreatedAt())
                .build();
    }

    public Warehouse toEntity(WarehouseCreateRequest request) {
        if (request == null) {
            return null;
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setType(request.getType());
        warehouse.setAddress(request.getAddress());
        warehouse.setProvince(request.getProvince());

        if (request.getLatitude() != null && request.getLongitude() != null) {
            Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            warehouse.setLocation(location);
        }

        return warehouse;
    }

    public void updateEntity(WarehouseUpdateRequest request, Warehouse warehouse) {
        if (request == null || warehouse == null) {
            return;
        }

        warehouse.setName(request.getName());
        warehouse.setType(request.getType());
        warehouse.setAddress(request.getAddress());
        warehouse.setProvince(request.getProvince());

        if (request.getLatitude() != null && request.getLongitude() != null) {
            Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            warehouse.setLocation(location);
        }
    }
}
