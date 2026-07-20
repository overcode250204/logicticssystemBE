package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.enums.NotificationType;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.repositories.*;
import com.overcode250204.smartlogicticssystem.services.IRoutingEngineService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import com.overcode250204.smartlogicticssystem.utils.BarcodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;



    @Override
    @Transactional
    public void checkRoutingCondition(Long routeId) {
        RouteConfig route = routeConfigRepository.findById(routeId).orElse(null);
        if (route == null) return;
        if (route.getIsActive() != null && !route.getIsActive()) return;

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

    @Transactional
    protected void triggerPalletization(RouteConfig route, List<Order> eligibleOrders) {
        log.info("Triggering palletization for Route: {}", route.getRouteName());

        // Create Shipment Batch (LinehaulTrip)
        LinehaulTrip trip = new LinehaulTrip();
        trip.setLinehaulTripCode(generateUniqueLinehaulTripCode());
        trip.setRouteConfig(route);
        Vehicle vehicle = route.getDefaultVehicle();
        if (vehicle != null) {
            trip.setVehicle(vehicle);
            vehicle.setStatus(VehicleStatus.ON_TRIP);
            vehicleRepository.save(vehicle);
        }
        trip.setStatus(LinehaulTripStatus.PREPARING);
        trip.setIsCreatedSystem(true);
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
        pallet.setRouteConfig(route);
        pallet.setIsCreatedSystem(true);

        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;

        for (Order order : eligibleOrders) {
            order.setStatus(OrderStatus.READY_TO_PICK);
            PalletItem item = new PalletItem();
            item.setOrder(order);
            item.setPallet(pallet);
            pallet.getPalletItems().add(item);
            orderRepository.save(order);

            if (order.getTotalWeightKg() != null) {
                totalWeight = totalWeight.add(order.getTotalWeightKg());
            }
            if (order.getTotalVolumeM3() != null) {
                totalVolume = totalVolume.add(order.getTotalVolumeM3());
            }
        }
        pallet.setTotalWeightKg(totalWeight);
        pallet.setTotalVolumeM3(totalVolume);
        palletRepository.save(pallet);
        sendPalletizationTaskNotifications(route, trip, pallet, eligibleOrders.size());
    }

    private void sendPalletizationTaskNotifications(RouteConfig route, LinehaulTrip trip, Pallet pallet, int orderCount) {
        List<User> staffUsers = userRepository.findByRole_RoleIdInAndIsActiveTrue(List.of(4));

        for (User staff : staffUsers) {
            Notification notification = new Notification();
            notification.setTitle("Đóng gói pallet " + pallet.getPalletCode());
            notification.setMessage("%d đơn hàng cần đóng gói cho tuyến %s. Chuyến linehaul: %s"
                    .formatted(
                            orderCount,
                            route.getRouteName(),
                            trip.getLinehaulTripCode() != null ? trip.getLinehaulTripCode() : trip.getLinehaulId()
                    ));
            notification.setType(NotificationType.PALLETIZATION_TASK);
            notification.setRecipientId(staff.getUserId());
            notification.setReferenceType("PALLET");
            notification.setReferenceId(pallet.getPalletId());
            notification.setIsRead(false);

            Notification savedNotification = notificationRepository.save(notification);
            String topic = "/topic/notifications/" + staff.getUserId();
            messagingTemplate.convertAndSend(topic, savedNotification);
        }

        log.info(
                "Sent palletization task notifications. palletId={}, palletCode={}, staffCount={}",
                pallet.getPalletId(),
                pallet.getPalletCode(),
                staffUsers.size()
        );
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
        } while (palletRepository.existsPalletByPalletCode(code));
        return code;
    }



    private String uploadBarcodeImage(BarcodeGeneratorUtil.GeneratedCode128Barcode generatedBarcode) {
        String key = "%s/%s.png".formatted("pallet-barcodes", generatedBarcode.barcode());
        return s3FileService.uploadBytes(generatedBarcode.pngBytes(), key, "image/png");
    }

    private String generateUniqueLinehaulTripCode() {
        String code;
        String chars = "0123456789";
        do {
            StringBuilder sb = new StringBuilder("LT-");
            for (int i = 0; i < 12; i++) {
                sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
            }
            code = sb.toString();
        } while (linehaulTripRepository.existsByLinehaulTripCode(code));
        return code;
    }

}
