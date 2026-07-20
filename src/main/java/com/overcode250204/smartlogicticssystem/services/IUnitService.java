package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.response.UnitResponseDTO;
import com.overcode250204.smartlogicticssystem.enums.UnitType;

import java.util.List;

public interface IUnitService {
    List<UnitResponseDTO> getAllUnits(UnitType type);
}
