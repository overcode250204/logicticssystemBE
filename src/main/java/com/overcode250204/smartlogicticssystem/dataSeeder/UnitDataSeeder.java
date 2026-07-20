package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Unit;
import com.overcode250204.smartlogicticssystem.enums.UnitType;
import com.overcode250204.smartlogicticssystem.repositories.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnitDataSeeder implements DataSeeder {

    private final UnitRepository unitRepository;

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    @Transactional
    public void seed() {
        createUnitIfNotExists("KG", "Kilogram", UnitType.WEIGHT);
        createUnitIfNotExists("G", "Gram", UnitType.WEIGHT);

        createUnitIfNotExists("M", "Meter", UnitType.DIMENSION);
        createUnitIfNotExists("CM", "Centimeter", UnitType.DIMENSION);

        createUnitIfNotExists("M3", "Cubic meter", UnitType.VOLUME);
        createUnitIfNotExists("L", "Liter", UnitType.VOLUME);
        createUnitIfNotExists("ML", "Milliliter", UnitType.VOLUME);

        createUnitIfNotExists("PCS", "Piece", UnitType.QUANTITY);
        createUnitIfNotExists("BOX", "Box", UnitType.QUANTITY);

        log.info("Unit seed data completed.");
    }

    private void createUnitIfNotExists(String code, String name, UnitType type) {
        boolean exists = unitRepository.findByCodeIgnoreCase(code).isPresent();

        if (exists) {
            log.info("Unit already exists: {}", code);
            return;
        }

        Unit unit = new Unit();
        unit.setCode(code);
        unit.setName(name);
        unit.setType(type);

        unitRepository.save(unit);

        log.info("Created unit: {}", code);
    }
}
