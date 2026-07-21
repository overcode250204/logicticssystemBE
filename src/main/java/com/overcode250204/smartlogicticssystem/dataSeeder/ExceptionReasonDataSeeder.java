package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.ExceptionReason;
import com.overcode250204.smartlogicticssystem.repositories.ExceptionReasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionReasonDataSeeder implements DataSeeder {

    private final ExceptionReasonRepository exceptionReasonRepository;

    @Override
    public int getOrder() {
        return 14;
    }

    @Override
    @Transactional
    public void seed() {
        List<ReasonSeed> reasons = List.of(
                new ReasonSeed("CUSTOMER", "Customer was not available"),
                new ReasonSeed("CUSTOMER", "Customer refused the delivery"),
                new ReasonSeed("ADDRESS", "Delivery address could not be located"),
                new ReasonSeed("ADDRESS", "Delivery address was outside the service area"),
                new ReasonSeed("PRODUCT", "Package was damaged during transport"),
                new ReasonSeed("PRODUCT", "Product quantity did not match the order"),
                new ReasonSeed("VEHICLE", "Vehicle failure interrupted the trip"),
                new ReasonSeed("WEATHER", "Delivery was delayed by severe weather")
        );

        reasons.forEach(this::createReasonIfNotExists);
        log.info("Exception-reason seed data completed.");
    }

    private void createReasonIfNotExists(ReasonSeed seed) {
        if (exceptionReasonRepository
                .findByCategoryIgnoreCaseAndReasonTextIgnoreCase(seed.category(), seed.reasonText())
                .isPresent()) {
            return;
        }

        ExceptionReason reason = new ExceptionReason();
        reason.setCategory(seed.category());
        reason.setReasonText(seed.reasonText());
        reason.setIsActive(true);
        exceptionReasonRepository.save(reason);
    }

    private record ReasonSeed(String category, String reasonText) {
    }
}
