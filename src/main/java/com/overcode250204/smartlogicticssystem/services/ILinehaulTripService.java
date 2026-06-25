package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;

public interface ILinehaulTripService {

    LinehaulTripResponseDTO update(Long id, LinehaulTripUpdateRequest request, int roleId, int userId);
}
