package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.enums.NotificationType;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.repositories.*;
import com.overcode250204.smartlogicticssystem.services.IRoutingEngineService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import com.overcode250204.smartlogicticssystem.utils.BarcodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutingEngineServiceImpl implements IRoutingEngineService {

    private final RouteConfigRepository routeConfigRepository;
    private final OrderRepository orderRepository;
    private final LinehaulTripRepository linehaulTripRepository;
    private final PalletRepository palletRepository;
    private final NotificationRepository notificationRepository;
    private final S3FileService s3FileService;


    //TODO CHECK ACTIVE DRIVER
    //TODO CHECK ROLE
    //TODO CHECK GPS FOR LINEHAUL TRIP
    /*
    TODO DRIVER TRIGGER UPDATE STATUS OF LINEHAUL TRIP -> TO NOTIFICATION
     TODO IF TYPE LINEHAUL IF ARRIVED -> STAFF MUST SCAN BARCODE TO OPEN PALLET
     TODO AND SCAN ORDER TO CHECK -> CHANGE STATUS OF ORDER  TO ARRIVED_AT_HUB
     TODO IF LOSS ORDER -> STAFF MUST REPORT
     TODO WHEN SCAN ALL OF ORDER -> STAFF TRIGGER FINISH TO NOTIFICATION
     TODO AND SYSTEM WILL CREATE LOCAL TRIP FOR DRIVER AND RUN VRP
     TODO LAST MILE DRIVER MUST FOLLOW STEP: CHANGE STATUS ORDER TO   IN_TRANSIT_LOCAL
     TODO WHEN CHANGE TO ARRIVED_AT_DELIVERY_POINT MUST CHECK GPS
     TODO DRIVER MUST CAPTURE BILL SEND TO SYSTEM. 2 TYPE PAYMENT COD -> MANAGE MAUNUAL. CREDIT -> MUST TO TRACKING IN SYSTEM
     TODO IF FAIL -> ORDER MUST RESTORE CNC
     TODO WHEN LOCAL TRIP FINISH MUST CHANGE LOCAL TRIP STATUS TO COMPLETED
     ====================
     TODO IF DRIVER HAVE PROBLEM. MUST ALLOW STAF CAN CHANGE DRIVER FOR THIS TRIP MANUAL
     ===================
     TODO IF HAVE FAILED ORDER
     TODO WHEN LINEHAUL TRIP COME THIS ORDER WILL RETURN IN THIS VEHICLE.
     */
    @Override
    @Transactional
    public void checkRoutingCondition(Long routeId) {
        RouteConfig route = routeConfigRepository.findById(routeId).orElse(null);
        if (route == null) return;

        List<Order> newOrders = orderRepository.findByRouteConfigAndStatusOrderByCreatedAtAsc(route, OrderStatus.NEW);
        if (newOrders.isEmpty()) return;

        boolean shouldTrigger = false;

        if (route.getDispatchType() != null) {
            switch (route.getDispatchType()) {
                case TIME:
                    shouldTrigger = checkTimeCondition(route);
                    break;
                case CAPACITY:
                    shouldTrigger = checkCapacityCondition(route, newOrders);
                    break;
                case HYBRID:
                    shouldTrigger = checkTimeCondition(route) || 
                                    checkHybridWaitTimeCondition(route, newOrders) || 
                                    checkCapacityCondition(route, newOrders);
                    break;
            }
        }

        if (shouldTrigger) {
            triggerPalletization(route, newOrders);
        }
    }

    private boolean checkTimeCondition(RouteConfig route) {
        if (route.getCutoffTime() == null || route.getFixedDispatchTime() == null) return false;
        
        LocalTime now = LocalTime.now();
        LocalTime cutoff = route.getCutoffTime();
        LocalTime dispatch = route.getFixedDispatchTime();

        if (cutoff.isBefore(dispatch) || cutoff.equals(dispatch)) {
            return !now.isBefore(cutoff) && !now.isAfter(dispatch);
        } else {
            // Handles cases crossing midnight
            return !now.isBefore(cutoff) || !now.isAfter(dispatch);
        }
    }

    private boolean checkHybridWaitTimeCondition(RouteConfig route, List<Order> newOrders) {
        if (route.getMaxWaitingDays() == null || newOrders.isEmpty()) return false;
        Order oldestOrder = newOrders.getFirst();
        LocalDateTime threshold = oldestOrder.getCreatedAt().plusDays(route.getMaxWaitingDays());
        return LocalDateTime.now().isAfter(threshold);
    }

    private boolean checkCapacityCondition(RouteConfig route, List<Order> newOrders) {
        if (route.getDefaultVehicle() == null || route.getMinCapacityPercentage() == null) return false;

        BigDecimal totalWeight = newOrders.stream()
                .map(Order::getTotalWeightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVolume = newOrders.stream()
                .map(Order::getTotalVolumeM3)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Vehicle vehicle = route.getDefaultVehicle();
        
        if (vehicle.getMaxWeightKg() != null && vehicle.getMaxWeightKg().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal weightRatio = totalWeight.divide(vehicle.getMaxWeightKg(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            if (weightRatio.compareTo(new BigDecimal(route.getMinCapacityPercentage())) >= 0) {
                return true;
            }
        }

        if (vehicle.getMaxVolumeM3() != null && vehicle.getMaxVolumeM3().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal volumeRatio = totalVolume.divide(vehicle.getMaxVolumeM3(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            if (volumeRatio.compareTo(new BigDecimal(route.getMinCapacityPercentage())) >= 0) {
                return true;
            }
        }

        return false;
    }

    private void triggerPalletization(RouteConfig route, List<Order> eligibleOrders) {
        log.info("Triggering palletization for Route: {}", route.getRouteName());

        // Create Shipment Batch (LinehaulTrip) - TODO FOR ADMIN UPDATE FLEXIBLE
        LinehaulTrip trip = new LinehaulTrip();
        trip.setRouteConfig(route);
        trip.setVehicle(route.getDefaultVehicle());
        trip.setStatus(LinehaulTripStatus.PREPARING);
        trip = linehaulTripRepository.save(trip);

        // Update Orders and create empty Pallet
        Pallet pallet = new Pallet();
        pallet.setLinehaulTrip(trip);
        pallet = palletRepository.save(pallet);
        // Generate Order Code
        String palletCode = generateUniqueOrderCode();
        pallet.setPalletCode(palletCode);

        // Generate Barcode using the exact order code
        String barcodeData = palletCode;
        BarcodeGeneratorUtil.GeneratedCode128Barcode generatedBarcode = BarcodeGeneratorUtil.generateCode128Barcode(barcodeData);
        String barcodeImageUrl = uploadBarcodeImage(generatedBarcode);
        pallet.setBarcodeUrl(barcodeImageUrl);

        for (Order order : eligibleOrders) {
            order.setStatus(OrderStatus.READY_TO_PICK);
            orderRepository.save(order);
        }

        // Send Notification to Hub Manager
        Notification notification = new Notification();
        notification.setTitle("Palletization Task: " + route.getRouteName());
        notification.setMessage("Palletization triggered for " + eligibleOrders.size() + " orders. Linehaul Trip ID: " + trip.getLinehaulId());
        notification.setType(NotificationType.PALLETIZATION_TASK);
        notificationRepository.save(notification);
    }

    private String generateUniqueOrderCode() {
        String code;
        String chars = "0123456789";
        do {
            StringBuilder sb = new StringBuilder("PL-");
            for (int i = 0; i < 12; i++) {
                sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
            }
            code = sb.toString();
        } while (orderRepository.existsByOrderCode(code));
        return code;
    }



    private String uploadBarcodeImage(BarcodeGeneratorUtil.GeneratedCode128Barcode generatedBarcode) {
        String key = "%s/%s.png".formatted("pallet-barcodes", generatedBarcode.barcode());
        return s3FileService.uploadBytes(generatedBarcode.pngBytes(), key, "image/png");
    }

}
