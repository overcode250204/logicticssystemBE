package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.response.UnitResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Unit;
import com.overcode250204.smartlogicticssystem.enums.UnitType;
import com.overcode250204.smartlogicticssystem.repositories.UnitRepository;
import com.overcode250204.smartlogicticssystem.services.IUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements IUnitService {

    private final UnitRepository unitRepository;

    @Override
    public List<UnitResponseDTO> getAllUnits(UnitType type) {
        return unitRepository.findAll().stream()
                .filter(unit -> type == null || unit.getType() == type)
                .map(this::toResponse)
                .toList();
    }

    private UnitResponseDTO toResponse(Unit unit) {
        return UnitResponseDTO.builder()
                .id(unit.getId())
                .code(unit.getCode())
                .name(unit.getName())
                .type(unit.getType())
                .build();
    }
}
