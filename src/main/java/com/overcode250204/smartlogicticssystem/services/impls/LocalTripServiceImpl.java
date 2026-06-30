package com.overcode250204.smartlogicticssystem.services.impls;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.enums.*;
import com.overcode250204.smartlogicticssystem.repositories.*;
import com.overcode250204.smartlogicticssystem.services.ILocalTripService;
import com.overcode250204.smartlogicticssystem.vrp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalTripServiceImpl implements ILocalTripService {

    private final LocalTripRepository localTripRepository;
    private final LocalTripDetailRepository localTripDetailRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final OsrmRoutingService osrmRoutingService;

    @Override
    @Transactional
    public List<LocalTrip> planLocalTrips(Long zoneId, List<Long> orderIds) {
        List<Order> eligibleOrders = orderRepository.findAllById(orderIds).stream()
                .filter(o -> o.getZone() != null && o.getZone().getZoneId().equals(zoneId))
                .filter(o -> o.getStatus() == OrderStatus.ARRIVED_AT_HUB || o.getStatus() == OrderStatus.READY_TO_PICK)
                .collect(Collectors.toList());

        if (eligibleOrders.isEmpty()) {
            throw new IllegalArgumentException("No eligible orders found in this zone.");
        }

        List<Driver> availableDrivers = driverRepository.findByZone_ZoneIdAndStatus(zoneId, DriverStatus.AVAILABLE)
                .stream().filter(d -> d.getCurrentVehicle() != null).collect(Collectors.toList());

        if (availableDrivers.isEmpty()) {
            throw new IllegalStateException("No available drivers with vehicles in this zone.");
        }

        // Build VRP Model
        List<DeliveryLocation> locationList = new ArrayList<>();
        List<DeliveryOrder> orderList = new ArrayList<>();

        DeliveryLocation hubLocation = new DeliveryLocation(0L, 10.762622, 106.660172); // Default Hub, replace with real hub location
        locationList.add(hubLocation);

        for (Order order : eligibleOrders) {
            DeliveryLocation loc = new DeliveryLocation(order.getOrderId(), order.getDeliveryPoint().getY(), order.getDeliveryPoint().getX());
            locationList.add(loc);
            DeliveryOrder deliveryOrder = new DeliveryOrder(order.getOrderId(), loc, order.getTotalWeightKg(), order.getTotalVolumeM3());
            orderList.add(deliveryOrder);
        }

        // Calculate Distance Matrix
        double[][] distanceMatrix = osrmRoutingService.getDistanceMatrix(locationList);
        for (int i = 0; i < locationList.size(); i++) {
            Map<DeliveryLocation, Double> distMap = new HashMap<>();
            for (int j = 0; j < locationList.size(); j++) {
                if (i != j) {
                    distMap.put(locationList.get(j), distanceMatrix[i][j]);
                }
            }
            locationList.get(i).setDistanceMap(distMap);
        }

        List<DeliveryVehicle> vehicleList = new ArrayList<>();
        for (Driver driver : availableDrivers) {
            DeliveryVehicle vehicle = new DeliveryVehicle(driver.getCurrentVehicle().getVehicleId(), driver.getDriverId(), hubLocation,
                    driver.getCurrentVehicle().getMaxWeightKg(), driver.getCurrentVehicle().getMaxVolumeM3(), new ArrayList<>());
            vehicleList.add(vehicle);
        }

        DeliverySolution problem = new DeliverySolution(locationList, orderList, vehicleList);

        // Solve VRP
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(DeliverySolution.class)
                .withEntityClasses(DeliveryVehicle.class)
                .withConstraintProviderClass(DeliveryConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(10));
        SolverFactory<DeliverySolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<DeliverySolution> solver = solverFactory.buildSolver();

        DeliverySolution solution = solver.solve(problem);

        List<LocalTrip> createdTrips = new ArrayList<>();
        for (DeliveryVehicle vrpVehicle : solution.getVehicleList()) {
            if (vrpVehicle.getOrders() != null && !vrpVehicle.getOrders().isEmpty()) {
                Driver driver = driverRepository.findById(vrpVehicle.getDriverId()).orElseThrow();
                LocalTrip trip = new LocalTrip();
                trip.setDriver(driver);
                trip.setVehicle(driver.getCurrentVehicle());
                trip.setStatus(LocalTripStatus.PENDING_ACCEPTANCE);
                trip = localTripRepository.save(trip);

                int stopOrder = 1;
                for (DeliveryOrder vrpOrder : vrpVehicle.getOrders()) {
                    Order order = orderRepository.findById(vrpOrder.getOrderId()).orElseThrow();
                    LocalTripDetail detail = new LocalTripDetail();
                    detail.setLocalTrip(trip);
                    detail.setOrder(order);
                    detail.setStopOrder(stopOrder++);
                    detail.setStatus(LocalTripDetailStatus.PENDING);
                    localTripDetailRepository.save(detail);

                    order.setStatus(OrderStatus.IN_TRANSIT_LOCAL);
                    orderRepository.save(order);
                }
                createdTrips.add(trip);
                
                driver.setStatus(DriverStatus.ON_TRIP);
                driverRepository.save(driver);
            }
        }
        return createdTrips;
    }

    @Override
    @Transactional
    public void acceptTrip(Long driverId, Long tripId) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (!trip.getDriver().getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Driver mismatch.");
        }
        if (trip.getStatus() != LocalTripStatus.PENDING_ACCEPTANCE) {
            throw new IllegalStateException("Trip is not in PENDING_ACCEPTANCE status.");
        }
        trip.setStatus(LocalTripStatus.ACCEPTED);
        localTripRepository.save(trip);
    }

    @Override
    @Transactional
    public void cancelTrip(Long driverId, Long tripId) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (!trip.getDriver().getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Driver mismatch.");
        }
        if (trip.getStatus() != LocalTripStatus.PENDING_ACCEPTANCE && trip.getStatus() != LocalTripStatus.ACCEPTED) {
            throw new IllegalStateException("Cannot cancel trip in current status.");
        }
        trip.setStatus(LocalTripStatus.CANCELLED);
        localTripRepository.save(trip);

        Driver driver = trip.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
        
        List<LocalTripDetail> details = localTripDetailRepository.findByLocalTrip_LocalTripIdOrderByStopOrderAsc(tripId);
        for(LocalTripDetail detail : details) {
             Order o = detail.getOrder();
             o.setStatus(OrderStatus.ARRIVED_AT_HUB); // Revert status
             orderRepository.save(o);
        }
    }

    @Override
    @Transactional
    public void collapseTrip(Long cancelledTripId, Long targetTripId) {
        LocalTrip cancelledTrip = localTripRepository.findById(cancelledTripId).orElseThrow();
        LocalTrip targetTrip = localTripRepository.findById(targetTripId).orElseThrow();

        if (cancelledTrip.getStatus() != LocalTripStatus.CANCELLED) {
            throw new IllegalStateException("Source trip must be cancelled.");
        }
        if (targetTrip.getStatus() == LocalTripStatus.EXECUTING || targetTrip.getStatus() == LocalTripStatus.COMPLETED) {
             throw new IllegalStateException("Target trip cannot be already executing or completed.");
        }

        List<LocalTripDetail> cancelledDetails = localTripDetailRepository.findByLocalTrip_LocalTripIdOrderByStopOrderAsc(cancelledTripId);
        List<LocalTripDetail> targetDetails = localTripDetailRepository.findByLocalTrip_LocalTripIdOrderByStopOrderAsc(targetTripId);
        
        List<Long> allOrderIds = new ArrayList<>();
        cancelledDetails.forEach(d -> allOrderIds.add(d.getOrder().getOrderId()));
        targetDetails.forEach(d -> allOrderIds.add(d.getOrder().getOrderId()));

        // Delete existing details for target trip to recreate them
        localTripDetailRepository.deleteAll(targetDetails);
        localTripDetailRepository.deleteAll(cancelledDetails);

        List<Order> orders = orderRepository.findAllById(allOrderIds);

        // Run VRP just for this vehicle and these orders
        List<DeliveryLocation> locationList = new ArrayList<>();
        List<DeliveryOrder> orderList = new ArrayList<>();
        DeliveryLocation hubLocation = new DeliveryLocation(0L, 10.762622, 106.660172);
        locationList.add(hubLocation);

        for (Order order : orders) {
            DeliveryLocation loc = new DeliveryLocation(order.getOrderId(), order.getDeliveryPoint().getY(), order.getDeliveryPoint().getX());
            locationList.add(loc);
            DeliveryOrder deliveryOrder = new DeliveryOrder(order.getOrderId(), loc, order.getTotalWeightKg(), order.getTotalVolumeM3());
            orderList.add(deliveryOrder);
        }

        double[][] distanceMatrix = osrmRoutingService.getDistanceMatrix(locationList);
        for (int i = 0; i < locationList.size(); i++) {
            Map<DeliveryLocation, Double> distMap = new HashMap<>();
            for (int j = 0; j < locationList.size(); j++) {
                if (i != j) distMap.put(locationList.get(j), distanceMatrix[i][j]);
            }
            locationList.get(i).setDistanceMap(distMap);
        }

        Driver driver = targetTrip.getDriver();
        DeliveryVehicle vehicle = new DeliveryVehicle(driver.getCurrentVehicle().getVehicleId(), driver.getDriverId(), hubLocation,
                driver.getCurrentVehicle().getMaxWeightKg(), driver.getCurrentVehicle().getMaxVolumeM3(), new ArrayList<>());

        DeliverySolution problem = new DeliverySolution(locationList, orderList, Collections.singletonList(vehicle));

        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(DeliverySolution.class)
                .withEntityClasses(DeliveryVehicle.class)
                .withConstraintProviderClass(DeliveryConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(5));
        Solver<DeliverySolution> solver = SolverFactory.<DeliverySolution>create(solverConfig).buildSolver();

        DeliverySolution solution = solver.solve(problem);

        DeliveryVehicle solvedVehicle = solution.getVehicleList().get(0);
        int stopOrder = 1;
        for (DeliveryOrder vrpOrder : solvedVehicle.getOrders()) {
            Order order = orderRepository.findById(vrpOrder.getOrderId()).orElseThrow();
            LocalTripDetail detail = new LocalTripDetail();
            detail.setLocalTrip(targetTrip);
            detail.setOrder(order);
            detail.setStopOrder(stopOrder++);
            detail.setStatus(LocalTripDetailStatus.PENDING);
            localTripDetailRepository.save(detail);
            
            order.setStatus(OrderStatus.IN_TRANSIT_LOCAL);
            orderRepository.save(order);
        }
        
        targetTrip.setStatus(LocalTripStatus.PENDING_ACCEPTANCE);
        localTripRepository.save(targetTrip);
    }

    @Override
    @Transactional
    public void changeVehicle(Long tripId, Long newVehicleId) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (trip.getStatus() == LocalTripStatus.EXECUTING || trip.getStatus() == LocalTripStatus.COMPLETED) {
             throw new IllegalStateException("Cannot change vehicle for executing/completed trip.");
        }
        Vehicle vehicle = vehicleRepository.findById(newVehicleId).orElseThrow();
        // Check if vehicle is available and at the same hub as the driver...
        // For simplicity, just updating it here
        trip.setVehicle(vehicle);
        trip.getDriver().setCurrentVehicle(vehicle);
        driverRepository.save(trip.getDriver());
        localTripRepository.save(trip);
    }

    @Override
    @Transactional
    public void scanBarcode(Long tripId, Long orderId, String barcode) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (!order.getOrderCode().equals(barcode)) {
            throw new IllegalArgumentException("Barcode does not match order.");
        }
        List<LocalTripDetail> details = localTripDetailRepository.findByLocalTrip_LocalTripIdOrderByStopOrderAsc(tripId);
        for (LocalTripDetail detail : details) {
            if (detail.getOrder().getOrderId().equals(orderId)) {
                detail.setBarcodeScanned(true);
                localTripDetailRepository.save(detail);
                return;
            }
        }
        throw new IllegalArgumentException("Order not in this trip.");
    }

    @Override
    @Transactional
    public void startExecuting(Long driverId, Long tripId) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (!trip.getDriver().getDriverId().equals(driverId)) throw new IllegalArgumentException("Driver mismatch.");
        if (trip.getStatus() != LocalTripStatus.ACCEPTED) throw new IllegalStateException("Trip must be ACCEPTED first.");
        
        List<LocalTripDetail> details = localTripDetailRepository.findByLocalTrip_LocalTripIdOrderByStopOrderAsc(tripId);
        for (LocalTripDetail detail : details) {
            if (Boolean.FALSE.equals(detail.getBarcodeScanned())) {
                throw new IllegalStateException("All orders must be scanned before executing.");
            }
        }
        
        trip.setStatus(LocalTripStatus.EXECUTING);
        localTripRepository.save(trip);
    }

    @Override
    @Transactional
    public void arriveAtPoint(Long driverId, Long detailId, double lat, double lon) {
        LocalTripDetail detail = localTripDetailRepository.findById(detailId).orElseThrow();
        if (!detail.getLocalTrip().getDriver().getDriverId().equals(driverId)) throw new IllegalArgumentException("Driver mismatch.");
        
        Order order = detail.getOrder();
        double distance = calculateHaversineDistance(lat, lon, order.getDeliveryPoint().getY(), order.getDeliveryPoint().getX());
        if (distance > 500) {
            throw new IllegalStateException("Not within 500m of the delivery point. Distance: " + distance + "m");
        }
        
        detail.setStatus(LocalTripDetailStatus.ARRIVED);
        localTripDetailRepository.save(detail);
    }

    @Override
    @Transactional
    public void completePoint(Long driverId, Long detailId, String proofUrl) {
        LocalTripDetail detail = localTripDetailRepository.findById(detailId).orElseThrow();
        if (!detail.getLocalTrip().getDriver().getDriverId().equals(driverId)) throw new IllegalArgumentException("Driver mismatch.");
        if (detail.getStatus() != LocalTripDetailStatus.ARRIVED) throw new IllegalStateException("Must arrive at point first.");
        
        detail.setStatus(LocalTripDetailStatus.COMPLETED);
        detail.setProofUrl(proofUrl);
        localTripDetailRepository.save(detail);
        
        Order order = detail.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        
        checkTripCompletion(detail.getLocalTrip().getLocalTripId());
    }

    @Override
    @Transactional
    public void failPoint(Long driverId, Long detailId, String proofUrl) {
        LocalTripDetail detail = localTripDetailRepository.findById(detailId).orElseThrow();
        if (!detail.getLocalTrip().getDriver().getDriverId().equals(driverId)) throw new IllegalArgumentException("Driver mismatch.");
        
        detail.setStatus(LocalTripDetailStatus.FAILED);
        detail.setProofUrl(proofUrl);
        localTripDetailRepository.save(detail);
        
        Order order = detail.getOrder();
        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);
        
        checkTripCompletion(detail.getLocalTrip().getLocalTripId());
    }
    
    private void checkTripCompletion(Long tripId) {
        List<LocalTripDetail> details = localTripDetailRepository.findByLocalTrip_LocalTripIdOrderByStopOrderAsc(tripId);
        boolean allDone = details.stream().allMatch(d -> d.getStatus() == LocalTripDetailStatus.COMPLETED || d.getStatus() == LocalTripDetailStatus.FAILED);
        if (allDone) {
            LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
            trip.setStatus(LocalTripStatus.COMPLETED);
            localTripRepository.save(trip);
            
            Driver driver = trip.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);
        }
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
