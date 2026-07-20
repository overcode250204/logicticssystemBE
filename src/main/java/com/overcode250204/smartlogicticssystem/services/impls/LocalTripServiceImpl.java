package com.overcode250204.smartlogicticssystem.services.impls;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import com.overcode250204.smartlogicticssystem.dtos.response.LocalTripResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.enums.*;
import com.overcode250204.smartlogicticssystem.mapper.LocalTripMapper;
import com.overcode250204.smartlogicticssystem.repositories.*;
import com.overcode250204.smartlogicticssystem.services.ILocalTripService;
import com.overcode250204.smartlogicticssystem.vrp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.overcode250204.smartlogicticssystem.dtos.request.FailPointRequestDTO;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.ExceptionReasonErrorCode;
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
    private final ZoneRepository zoneRepository;
    private final LocalTripMapper localTripMapper;
    private final ExceptionReasonRepository exceptionReasonRepository;
    private final OrderExceptionRepository orderExceptionRepository;

    @Override
    @Transactional
//    @Scheduled(cron = "0 */1 * * * *")
    public List<LocalTripResponseDTO> planLocalTrips() {
        System.out.println("Run plan local trip...");
        List<Zone> zones = zoneRepository.findAll();
        List<LocalTrip> allCreatedTrips = new ArrayList<>();

        for (Zone zone : zones) {
            Long zoneId = zone.getZoneId();
            List<Order> eligibleOrders = orderRepository.findByZone_ZoneIdAndStatusIn(
                    zoneId, List.of(OrderStatus.ARRIVED_AT_HUB)
            );

            if (eligibleOrders.isEmpty()) {
                continue;
            }

            Order firstOrder = eligibleOrders.get(0);
            Warehouse orderWarehouse = firstOrder.getAssignedHub();

            final Warehouse targetWarehouse = orderWarehouse;
            List<Driver> availableDrivers = driverRepository.findByZone_ZoneIdAndStatus(zoneId, DriverStatus.AVAILABLE)
                    .stream()
                    .filter(d -> d.getCurrentVehicle() != null)
                    .filter(d -> d.getDriverType() == DriverType.LAST_MILE)
                    .filter(d -> targetWarehouse == null || (d.getCurrentWarehouse() != null && d.getCurrentWarehouse().getWarehouseId().equals(targetWarehouse.getWarehouseId())))
                    .collect(Collectors.toList());

            if (availableDrivers.isEmpty()) {
                log.warn("No available LAST_MILE drivers with vehicles in zone: {} matching warehouse: {}", zoneId, targetWarehouse != null ? targetWarehouse.getWarehouseId() : "null");
                continue;
            }

            double maxWeightCapacity = availableDrivers.stream()
                    .mapToDouble(d -> d.getCurrentVehicle().getMaxWeightKg().doubleValue())
                    .sum();
            double maxVolumeCapacity = availableDrivers.stream()
                    .mapToDouble(d -> d.getCurrentVehicle().getMaxVolumeM3().doubleValue())
                    .sum();

            eligibleOrders.sort(Comparator.comparing(Order::getCreatedAt));

            List<Order> selectedOrders = new ArrayList<>();
            double currentWeight = 0;
            double currentVolume = 0;
            for (Order order : eligibleOrders) {
                double oWeight = order.getTotalWeightKg().doubleValue();
                double oVolume = order.getTotalVolumeM3().doubleValue();
                if (currentWeight + oWeight <= maxWeightCapacity && currentVolume + oVolume <= maxVolumeCapacity) {
                    selectedOrders.add(order);
                    currentWeight += oWeight;
                    currentVolume += oVolume;
                }
            }

            if (selectedOrders.isEmpty()) {
                log.warn("No orders fit in available drivers capacity for zone: {}", zoneId);
                continue;
            }

            // Build VRP Model
            List<DeliveryLocation> locationList = new ArrayList<>();
            List<DeliveryOrder> orderList = new ArrayList<>();

            double hubLat = 10.762622;
            double hubLon = 106.660172;
            if (targetWarehouse != null && targetWarehouse.getLocation() != null) {
                hubLat = targetWarehouse.getLocation().getY();
                hubLon = targetWarehouse.getLocation().getX();
            }

            DeliveryLocation hubLocation = new DeliveryLocation(0L, hubLat, hubLon);
            locationList.add(hubLocation);

            for (Order order : selectedOrders) {
                DeliveryLocation loc = new DeliveryLocation(order.getOrderId(), order.getDeliveryPoint().getY(), order.getDeliveryPoint().getX());
                locationList.add(loc);
                Integer slaHours = order.getZone() != null ? order.getZone().getSlaHours() : null;
                DeliveryOrder deliveryOrder = new DeliveryOrder(order.getOrderId(), loc, order.getTotalWeightKg(), order.getTotalVolumeM3(), slaHours);
                orderList.add(deliveryOrder);
            }

            // Calculate Distance and Duration Matrix
            double[][] distanceMatrix = osrmRoutingService.getDistanceMatrix(locationList);
            double[][] durationMatrix = osrmRoutingService.getDurationMatrix(locationList);
            for (int i = 0; i < locationList.size(); i++) {
                Map<DeliveryLocation, Double> distMap = new HashMap<>();
                Map<DeliveryLocation, Double> durMap = new HashMap<>();
                for (int j = 0; j < locationList.size(); j++) {
                    if (i != j) {
                        distMap.put(locationList.get(j), distanceMatrix[i][j]);
                        durMap.put(locationList.get(j), durationMatrix[i][j]);
                    }
                }
                locationList.get(i).setDistanceMap(distMap);
                locationList.get(i).setDurationMap(durMap);
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

            for (DeliveryVehicle vrpVehicle : solution.getVehicleList()) {
                if (vrpVehicle.getOrders() != null && !vrpVehicle.getOrders().isEmpty()) {
                    Driver driver = driverRepository.findById(vrpVehicle.getDriverId()).orElseThrow();
                    LocalTrip trip = new LocalTrip();
                    trip.setDriver(driver);
                    trip.setVehicle(driver.getCurrentVehicle());
                    trip.setStatus(LocalTripStatus.PENDING_ACCEPTANCE);
                    trip.setLocalTripCode(generateUniqueLocalTripCode());
                    
                    double totalDurationSeconds = 0.0;
                    DeliveryLocation previousLocation = vrpVehicle.getStartLocation();
                    for (DeliveryOrder vrpOrder : vrpVehicle.getOrders()) {
                        totalDurationSeconds += previousLocation.getDurationTo(vrpOrder.getLocation());
                        previousLocation = vrpOrder.getLocation();
                    }
                    if (!vrpVehicle.getOrders().isEmpty()) {
                        totalDurationSeconds += previousLocation.getDurationTo(vrpVehicle.getStartLocation());
                    }
                    int estMinutes = (int) Math.ceil(totalDurationSeconds / 60.0);
                    trip.setVrpEstimatedMinutes(estMinutes);

                    trip = localTripRepository.save(trip);

                    int stopOrder = 1;
                    for (DeliveryOrder vrpOrder : vrpVehicle.getOrders()) {
                        Order order = orderRepository.findById(vrpOrder.getOrderId()).orElseThrow();
                        LocalTripDetail detail = new LocalTripDetail();
                        detail.setLocalTrip(trip);
                        detail.setOrder(order);
                        detail.setStopOrder(stopOrder++);
                        detail.setStatus(LocalTripDetailStatus.PENDING);
                        detail.setLocalTripDetailCode(generateUniqueLocalTripDetailCode());
                        localTripDetailRepository.save(detail);

                        order.setStatus(OrderStatus.IN_TRANSIT_LOCAL);
                        orderRepository.save(order);
                    }
                    allCreatedTrips.add(trip);

                    driver.setStatus(DriverStatus.BUSY);
                    driverRepository.save(driver);
                }
            }
        }
        return allCreatedTrips.stream().map(localTripMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalTripResponseDTO> getAllLocalTrips(com.overcode250204.smartlogicticssystem.enums.LocalTripStatus status) {
        if (status != null) {
            return localTripRepository.findByStatus(status).stream().map(localTripMapper::toResponseDTO).collect(Collectors.toList());
        }
        return localTripRepository.findAll().stream().map(localTripMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalTripResponseDTO> getLocalTripsByDriverId(Long driverId) {
        return localTripRepository.findByDriver_DriverId(driverId).stream().map(localTripMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalTripResponseDTO getLocalTripById(Long id) {
        LocalTrip trip = localTripRepository.findById(id).orElseThrow();
        return localTripMapper.toResponseDTO(trip);
    }

    @Override
    @Transactional
    public void changeDriver(Long tripId, Long driverId) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        Driver newDriver = driverRepository.findById(driverId).orElseThrow();
        trip.setDriver(newDriver);
        localTripRepository.save(trip);
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

        // Filter orders that fit with constraints of vehicle
        double maxWeightCapacity = targetTrip.getVehicle().getMaxWeightKg().doubleValue();
        double maxVolumeCapacity = targetTrip.getVehicle().getMaxVolumeM3().doubleValue();

        List<Order> selectedOrders = new ArrayList<>();
        List<Order> remainingOrders = new ArrayList<>();
        double currentWeight = 0;
        double currentVolume = 0;
        for (Order order : orders) {
            double oWeight = order.getTotalWeightKg().doubleValue();
            double oVolume = order.getTotalVolumeM3().doubleValue();
            if (currentWeight + oWeight <= maxWeightCapacity && currentVolume + oVolume <= maxVolumeCapacity) {
                selectedOrders.add(order);
                currentWeight += oWeight;
                currentVolume += oVolume;
            } else {
                remainingOrders.add(order);
            }
        }

        // Reset remaining orders status to ARRIVED_AT_HUB
        for (Order o : remainingOrders) {
            o.setStatus(OrderStatus.ARRIVED_AT_HUB);
            orderRepository.save(o);
        }

        // Run VRP just for this vehicle and these selected orders
        List<DeliveryLocation> locationList = new ArrayList<>();
        List<DeliveryOrder> orderList = new ArrayList<>();
        
        double hubLat = 10.762622;
        double hubLon = 106.660172;
        if (!selectedOrders.isEmpty()) {
            Order firstOrder = selectedOrders.get(0);
            Warehouse orderWarehouse = firstOrder.getAssignedHub();
            if (orderWarehouse != null && orderWarehouse.getLocation() != null) {
                hubLat = orderWarehouse.getLocation().getY();
                hubLon = orderWarehouse.getLocation().getX();
            }
        }
        
        DeliveryLocation hubLocation = new DeliveryLocation(0L, hubLat, hubLon);
        locationList.add(hubLocation);

        for (Order order : selectedOrders) {
            DeliveryLocation loc = new DeliveryLocation(order.getOrderId(), order.getDeliveryPoint().getY(), order.getDeliveryPoint().getX());
            locationList.add(loc);
            Integer slaHours = order.getZone() != null ? order.getZone().getSlaHours() : null;
            DeliveryOrder deliveryOrder = new DeliveryOrder(order.getOrderId(), loc, order.getTotalWeightKg(), order.getTotalVolumeM3(), slaHours);
            orderList.add(deliveryOrder);
        }

        double[][] distanceMatrix = osrmRoutingService.getDistanceMatrix(locationList);
        double[][] durationMatrix = osrmRoutingService.getDurationMatrix(locationList);
        for (int i = 0; i < locationList.size(); i++) {
            Map<DeliveryLocation, Double> distMap = new HashMap<>();
            Map<DeliveryLocation, Double> durMap = new HashMap<>();
            for (int j = 0; j < locationList.size(); j++) {
                if (i != j) {
                    distMap.put(locationList.get(j), distanceMatrix[i][j]);
                    durMap.put(locationList.get(j), durationMatrix[i][j]);
                }
            }
            locationList.get(i).setDistanceMap(distMap);
            locationList.get(i).setDurationMap(durMap);
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
            detail.setLocalTripDetailCode(generateUniqueLocalTripDetailCode());
            localTripDetailRepository.save(detail);
            
            order.setStatus(OrderStatus.IN_TRANSIT_LOCAL);
            orderRepository.save(order);
        }
        
        targetTrip.setStatus(LocalTripStatus.PENDING_ACCEPTANCE);
        
        double totalDurationSeconds = 0.0;
        DeliveryLocation previousLocation = solvedVehicle.getStartLocation();
        for (DeliveryOrder vrpOrder : solvedVehicle.getOrders()) {
            totalDurationSeconds += previousLocation.getDurationTo(vrpOrder.getLocation());
            previousLocation = vrpOrder.getLocation();
        }
        if (!solvedVehicle.getOrders().isEmpty()) {
            totalDurationSeconds += previousLocation.getDurationTo(solvedVehicle.getStartLocation());
        }
        int estMinutes = (int) Math.ceil(totalDurationSeconds / 60.0);
        targetTrip.setVrpEstimatedMinutes(estMinutes);

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
        
        // 1. Check constraint of vehicle must fit with total weight and volume of orders
        List<LocalTripDetail> details = localTripDetailRepository.findByLocalTrip_LocalTripIdOrderByStopOrderAsc(tripId);
        double totalWeight = 0;
        double totalVolume = 0;
        for (LocalTripDetail detail : details) {
            Order order = detail.getOrder();
            totalWeight += order.getTotalWeightKg().doubleValue();
            totalVolume += order.getTotalVolumeM3().doubleValue();
        }
        if (totalWeight > vehicle.getMaxWeightKg().doubleValue() || totalVolume > vehicle.getMaxVolumeM3().doubleValue()) {
            throw new IllegalArgumentException("The new vehicle does not have enough capacity for the trip's orders.");
        }

        // 2. Check status of vehicle (must be ACTIVE) and at the same hub/warehouse as the driver
        if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
            throw new IllegalStateException("Vehicle is not active.");
        }
        Warehouse driverWarehouse = trip.getDriver().getCurrentWarehouse();
        Warehouse vehicleWarehouse = vehicle.getCurrentWarehouse();
        if (driverWarehouse == null || vehicleWarehouse == null || !driverWarehouse.getWarehouseId().equals(vehicleWarehouse.getWarehouseId())) {
            throw new IllegalArgumentException("Vehicle must be at the same hub/warehouse as the driver.");
        }

        trip.setVehicle(vehicle);
        trip.getDriver().setCurrentVehicle(vehicle);
        driverRepository.save(trip.getDriver());
        localTripRepository.save(trip);
    }


    @Override
    @Transactional
    public void scanBarcode(Long driverId, Long tripId, Long orderId, String barcode) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (!trip.getDriver().getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Driver mismatch.");
        }
        if (trip.getStatus() != LocalTripStatus.ACCEPTED) {
            throw new IllegalStateException("Trip is not in ACCEPTED status.");
        }
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
        
        Driver driver = trip.getDriver();
        driver.setStatus(DriverStatus.ON_LOCAL_TRIP);
        driver.setCurrentWarehouse(null);
        driverRepository.save(driver);

        Vehicle vehicle = trip.getVehicle();
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.ON_TRIP);
            vehicle.setCurrentWarehouse(null);
            vehicleRepository.save(vehicle);
        }
    }

    @Override
    @Transactional
    public void arriveAtPoint(Long driverId, Long tripId, Long detailId, double lat, double lon) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (trip.getStatus() != LocalTripStatus.EXECUTING) {
            throw new IllegalStateException("Trip must be EXECUTING.");
        }
        LocalTripDetail detail = localTripDetailRepository.findById(detailId).orElseThrow();
        if (!detail.getLocalTrip().getLocalTripId().equals(tripId)) {
            throw new IllegalArgumentException("Detail does not belong to the specified trip.");
        }
        if (!detail.getLocalTrip().getDriver().getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Driver mismatch.");
        }
        
        Order order = detail.getOrder();
        double distance = calculateHaversineDistance(lat, lon, order.getDeliveryPoint().getY(), order.getDeliveryPoint().getX());
        if (distance > 500) {
            throw new IllegalStateException("Not within 500m of the delivery point. Distance: " + distance + "m");
        }
        
        detail.setStatus(LocalTripDetailStatus.ARRIVED);
        localTripDetailRepository.save(detail);

        order.setStatus(OrderStatus.ARRIVED_AT_DELIVERY_POINT);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void completePoint(Long driverId, Long tripId, Long detailId, String proofUrl) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (trip.getStatus() != LocalTripStatus.EXECUTING) {
            throw new IllegalStateException("Trip must be EXECUTING.");
        }
        LocalTripDetail detail = localTripDetailRepository.findById(detailId).orElseThrow();
        if (!detail.getLocalTrip().getLocalTripId().equals(tripId)) {
            throw new IllegalArgumentException("Detail does not belong to the specified trip.");
        }
        if (!detail.getLocalTrip().getDriver().getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Driver mismatch.");
        }
        if (detail.getStatus() != LocalTripDetailStatus.ARRIVED) {
            throw new IllegalStateException("Must arrive at point first.");
        }
        
        detail.setStatus(LocalTripDetailStatus.COMPLETED);
        detail.setProofUrl(proofUrl);
        localTripDetailRepository.save(detail);
        
        Order order = detail.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        order.setProofUrl(proofUrl);
        order.setActualDeliveryTime(java.time.LocalDateTime.now());
        orderRepository.save(order);
        
        checkTripCompletion(tripId);
    }

    @Override
    @Transactional
    public void failPoint(Long driverId, Long tripId, Long detailId, String proofUrl, FailPointRequestDTO data) {
        LocalTrip trip = localTripRepository.findById(tripId).orElseThrow();
        if (trip.getStatus() != LocalTripStatus.EXECUTING) {
            throw new IllegalStateException("Trip must be EXECUTING.");
        }
        LocalTripDetail detail = localTripDetailRepository.findById(detailId).orElseThrow();
        if (!detail.getLocalTrip().getLocalTripId().equals(tripId)) {
            throw new IllegalArgumentException("Detail does not belong to the specified trip.");
        }
        if (!detail.getLocalTrip().getDriver().getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Driver mismatch.");
        }
        
        ExceptionReason exceptionReason = exceptionReasonRepository.findById(data.getReasonId())
                .orElseThrow(() -> new AppException(ExceptionReasonErrorCode.EXCEPTION_REASON_NOT_FOUND));

        detail.setStatus(LocalTripDetailStatus.FAILED);
        detail.setProofUrl(proofUrl);
        localTripDetailRepository.save(detail);
        
        Order order = detail.getOrder();
        order.setStatus(OrderStatus.FAILED);
        order.setProofUrl(proofUrl);
        order.setActualDeliveryTime(java.time.LocalDateTime.now());
        orderRepository.save(order);
        
        Driver driver = trip.getDriver();
        User reportedBy = driver != null ? driver.getUser() : null;

        OrderException orderException = new OrderException();
        orderException.setOrder(order);
        orderException.setExceptionReason(exceptionReason);
        orderException.setReportedBy(reportedBy);
        orderException.setNotes(data.getNotes());
        orderException.setImageUrl(proofUrl);
        orderExceptionRepository.save(orderException);

        checkTripCompletion(tripId);
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
            
            Vehicle vehicle = trip.getVehicle();
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.ACTIVE);
            }
            
            if (!details.isEmpty()) {
                Warehouse hub = details.get(0).getOrder().getAssignedHub();
                driver.setCurrentWarehouse(hub);
                if (vehicle != null) {
                    vehicle.setCurrentWarehouse(hub);
                }
            }
            driverRepository.save(driver);
            if (vehicle != null) {
                vehicleRepository.save(vehicle);
            }
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

    private String generateUniqueLocalTripCode() {
        String code;
        String chars = "0123456789";
        do {
            StringBuilder sb = new StringBuilder("LOT-");
            for (int i = 0; i < 12; i++) {
                sb.append(chars.charAt(java.util.concurrent.ThreadLocalRandom.current().nextInt(chars.length())));
            }
            code = sb.toString();
        } while (localTripRepository.existsByLocalTripCode(code));
        return code;
    }

    private String generateUniqueLocalTripDetailCode() {
        String code;
        String chars = "0123456789";
        do {
            StringBuilder sb = new StringBuilder("LTD-");
            for (int i = 0; i < 12; i++) {
                sb.append(chars.charAt(java.util.concurrent.ThreadLocalRandom.current().nextInt(chars.length())));
            }
            code = sb.toString();
        } while (localTripDetailRepository.existsByLocalTripDetailCode(code));
        return code;
    }
}
