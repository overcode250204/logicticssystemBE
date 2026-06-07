package com.overcode250204.smartlogicticssystem.routing.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import com.overcode250204.smartlogicticssystem.routing.domain.DistanceMatrix;
import com.overcode250204.smartlogicticssystem.routing.domain.Driver;
import com.overcode250204.smartlogicticssystem.routing.domain.OrderPlaning;
import com.overcode250204.smartlogicticssystem.routing.domain.RoutePlanSolution;

import java.util.List;

public class RouteConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                vehicleCapacity(factory),
                minimizeTotalDistance(factory)
        };
    }

    // 1. Kiểm tra tải trọng xe
    private Constraint vehicleCapacity(ConstraintFactory factory) {
        return factory.forEach(Driver.class)
                // Tính tổng cân nặng của các đơn trong List của Driver đó
                .filter(driver -> {
                    double totalWeight = driver.getOrderPlaningList().stream().mapToDouble(OrderPlaning::getWeightKg).sum();
                    return totalWeight > driver.getMaxWeightCapacity();
                })
                .penalize(HardSoftScore.ONE_HARD,
                        driver -> {
                            double totalWeight = driver.getOrderPlaningList().stream().mapToDouble(OrderPlaning::getWeightKg).sum();
                            return (int) (totalWeight - driver.getMaxWeightCapacity());
                        })
                .asConstraint("Vehicle Capacity");
    }

    // 2. Tính tổng quãng đường di chuyển của chuỗi List trong Driver
    private Constraint minimizeTotalDistance(ConstraintFactory factory) {
        return factory.forEach(Driver.class)
                .filter(driver -> !driver.getOrderPlaningList().isEmpty())

                .join(DistanceMatrix.class)

                .penalize(HardSoftScore.ONE_SOFT, (driver, matrix) ->{
                     double totalTimeInSeconds = 0;
                    List<OrderPlaning> assignedOrderPlanings = driver.getOrderPlaningList();
                    totalTimeInSeconds +=  matrix.getDistance(driver.getDepot(), assignedOrderPlanings.getFirst());

                    for (int i = 0; i < assignedOrderPlanings.size() - 1; i++) {
                        OrderPlaning currentOrderPlaning = assignedOrderPlanings.get(i);
                        OrderPlaning nextOrderPlaning = assignedOrderPlanings.get(i + 1);
                        totalTimeInSeconds +=  matrix.getDistance(currentOrderPlaning, nextOrderPlaning);
                    }

                    OrderPlaning lastOrderPlaning = assignedOrderPlanings.getLast();
                    totalTimeInSeconds += matrix.getDistance(lastOrderPlaning, driver.getDepot());
                    return (int) totalTimeInSeconds;

                })
                .asConstraint("Minimize Total Distance");
    }
}