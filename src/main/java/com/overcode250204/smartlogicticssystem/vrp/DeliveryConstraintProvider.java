package com.overcode250204.smartlogicticssystem.vrp;

import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;

import java.math.BigDecimal;

public class DeliveryConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                vehicleWeightCapacity(constraintFactory),
                vehicleVolumeCapacity(constraintFactory),
                minimizeDistance(constraintFactory)
        };
    }

    protected Constraint vehicleWeightCapacity(ConstraintFactory factory) {
        return factory.forEach(DeliveryVehicle.class)
                .filter(vehicle -> {
                    BigDecimal totalWeight = vehicle.getOrders().stream()
                            .map(DeliveryOrder::getWeightKg)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return totalWeight.compareTo(vehicle.getMaxWeightKg()) > 0;
                })
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        vehicle -> {
                            BigDecimal totalWeight = vehicle.getOrders().stream()
                                    .map(DeliveryOrder::getWeightKg)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            return totalWeight.subtract(vehicle.getMaxWeightKg()).longValue();
                        })
                .asConstraint("vehicleWeightCapacity");
    }

    protected Constraint vehicleVolumeCapacity(ConstraintFactory factory) {
        return factory.forEach(DeliveryVehicle.class)
                .filter(vehicle -> {
                    BigDecimal totalVolume = vehicle.getOrders().stream()
                            .map(DeliveryOrder::getVolumeM3)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return totalVolume.compareTo(vehicle.getMaxVolumeM3()) > 0;
                })
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        vehicle -> {
                            BigDecimal totalVolume = vehicle.getOrders().stream()
                                    .map(DeliveryOrder::getVolumeM3)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            // Multiply by 1000 to keep some precision as it's a long score
                            return totalVolume.subtract(vehicle.getMaxVolumeM3()).multiply(new BigDecimal(1000)).longValue();
                        })
                .asConstraint("vehicleVolumeCapacity");
    }

    protected Constraint minimizeDistance(ConstraintFactory factory) {
        return factory.forEach(DeliveryVehicle.class)
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        vehicle -> {
                            long totalDistance = 0;
                            DeliveryLocation previousLocation = vehicle.getStartLocation();
                            for (DeliveryOrder order : vehicle.getOrders()) {
                                totalDistance += previousLocation.getDistanceTo(order.getLocation());
                                previousLocation = order.getLocation();
                            }
                            // Add distance back to the hub
                            if (!vehicle.getOrders().isEmpty()) {
                                totalDistance += previousLocation.getDistanceTo(vehicle.getStartLocation());
                            }
                            return totalDistance;
                        })
                .asConstraint("minimizeDistance");
    }
}
