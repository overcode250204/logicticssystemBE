package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.RouteConfigUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.RouteConfigResponseDTO;

import java.util.List;

public interface IRouteConfigService {
    List<RouteConfigResponseDTO> getAllRouteConfigs(int roleId, int userId);
    RouteConfigResponseDTO getById(Long id, int roleId, int userId);
    RouteConfigResponseDTO create(RouteConfigCreateRequest request, int roleId, int userId);
    RouteConfigResponseDTO update(Long id, RouteConfigUpdateRequest request, int roleId, int userId);
    void delete(Long id, int roleId, int userId);
}
