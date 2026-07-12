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
                minimizeDistance(constraintFactory),
                checkSlaConstraint(constraintFactory)
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

    protected Constraint checkSlaConstraint(ConstraintFactory factory) {
        return factory.forEach(DeliveryVehicle.class)
                .filter(vehicle -> {
                    double currentEta = 0.0;
                    DeliveryLocation previousLocation = vehicle.getStartLocation();
                    for (DeliveryOrder order : vehicle.getOrders()) {
                        double travelTimeSeconds = previousLocation.getDurationTo(order.getLocation());
                        double travelTimeMinutes = travelTimeSeconds / 60.0;
                        currentEta += travelTimeMinutes;
                        
                        Integer slaHours = order.getZoneSlaHours();
                        if (slaHours != null) {
                            double slaMinutes = slaHours * 60.0;
                            if (currentEta > slaMinutes) {
                                return true;
                            }
                        }
                        previousLocation = order.getLocation();
                    }
                    return false;
                })
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        vehicle -> {
                            long penalty = 0;
                            double currentEta = 0.0;
                            DeliveryLocation previousLocation = vehicle.getStartLocation();
                            for (DeliveryOrder order : vehicle.getOrders()) {
                                double travelTimeSeconds = previousLocation.getDurationTo(order.getLocation());
                                double travelTimeMinutes = travelTimeSeconds / 60.0;
                                currentEta += travelTimeMinutes;
                                
                                Integer slaHours = order.getZoneSlaHours();
                                if (slaHours != null) {
                                    double slaMinutes = slaHours * 60.0;
                                    if (currentEta > slaMinutes) {
                                        penalty += (long) (currentEta - slaMinutes);
                                    }
                                }
                                previousLocation = order.getLocation();
                            }
                            return penalty > 0 ? penalty : 1L;
                        })
                .asConstraint("checkSlaConstraint");
    }
}
