package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletItemCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletItemResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import com.overcode250204.smartlogicticssystem.exception.*;
import com.overcode250204.smartlogicticssystem.mapper.PalletItemMapper;
import com.overcode250204.smartlogicticssystem.mapper.PalletMapper;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletItemRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.IPalletService;
import com.overcode250204.smartlogicticssystem.services.NotificationRealtimeService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import com.overcode250204.smartlogicticssystem.utils.BarcodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class PalletServiceImpl extends BaseServiceImpl implements IPalletService {
    private static final int ADMIN_ROLE_ID = 1;
    private static final int STAFF_ROLE_ID = 4;

    private final S3FileService s3FileService;
    private final PalletRepository palletRepository;
    private final OrderRepository orderRepository;
    private final PalletMapper palletMapper;
    private final RouteConfigRepository routeConfigRepository;
    private final PalletItemMapper palletItemMapper;
    private final PalletItemRepository palletItemRepository;
    private final UserRepository userRepository;
    private final NotificationRealtimeService notificationRealtimeService;

    private void checkAdminRole(int roleId) {
        if (roleId != ADMIN_ROLE_ID) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    private void checkStaffOrAdminRole(int roleId) {
        if (roleId != ADMIN_ROLE_ID && roleId != STAFF_ROLE_ID) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    private String generateUniquePalletCode() {
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

    @Override
    @Transactional
    public PalletResponseDTO create(PalletCreateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);

        RouteConfig routeConfig = findByIdOrThrow(routeConfigRepository,request.getRouteConfigId(),RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);

        Pallet pallet = new Pallet();
        pallet.setRouteConfig(routeConfig);
        pallet.setStatus(PalletStatus.CREATING);
        // Đánh dấu là nhiệm vụ của staff: query task staff (list + detail) lọc theo
        // isCreatedSystem = true. Không set thì pallet admin tạo tay sẽ không hiện
        // trong danh sách nhiệm vụ của staff để quét.
        pallet.setIsCreatedSystem(true);

        String palletCode = generateUniquePalletCode();
        pallet.setPalletCode(palletCode);

        BarcodeGeneratorUtil.GeneratedCode128Barcode barcode = BarcodeGeneratorUtil.generateCode128Barcode(palletCode);
        String barcodeUrl = uploadBarcodeImage(barcode);
        pallet.setBarcodeUrl(barcodeUrl);

        Pallet saved = palletRepository.save(pallet);
        return palletMapper.toResponse(saved);
    }

    @Override
    public PalletResponseDTO getById(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        Pallet pallet = findByIdOrThrow(palletRepository, id, PalletErrorCode.PALLET_NOT_FOUND);
        return palletMapper.toResponse(pallet);
    }

    @Override
    public List<PalletResponseDTO> getAll(int roleId, int userId) {
        checkAdminRole(roleId);
        return palletRepository.findAll().stream()
                .map(palletMapper::toResponse)
                .toList();
    }

    @Override
    public List<PalletResponseDTO> getStaffTasks(int roleId, int userId) {
        checkStaffOrAdminRole(roleId);
        return palletRepository.findSystemTasksByStatusIn(List.of(PalletStatus.CREATING, PalletStatus.CAN_SEAL))
                .stream()
                .map(palletMapper::toResponse)
                .toList();
    }

    @Override
    public PalletResponseDTO getStaffTaskById(Long id, int roleId, int userId) {
        checkStaffOrAdminRole(roleId);
        Pallet pallet = palletRepository.findSystemTaskByIdWithItemsAndOrders(id)
                .orElseThrow(() -> new AppException(PalletErrorCode.PALLET_NOT_FOUND));
        return palletMapper.toResponse(pallet);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        Pallet pallet = findByIdOrThrow(palletRepository, id, PalletErrorCode.PALLET_NOT_FOUND);
        //Trip status must be PREPARING
        LinehaulTrip trip = pallet.getLinehaulTrip();
       if(trip != null && !LinehaulTripStatus.PREPARING.equals(trip.getStatus())){
           throw new AppException(PalletErrorCode.PALLET_CANNOT_DELETE);
       }
       //Release Order
        List<PalletItem> items = pallet.getPalletItems();
       for(PalletItem item : items){
           Order order = item.getOrder();
           if(order != null){
               order.setStatus(OrderStatus.NEW);
               orderRepository.save(order);
           }
       }
        palletRepository.delete(pallet);
    }


    @Override
    @Transactional
    public PalletItemResponseDTO addPalletItem(PalletItemCreateRequest request, Long palletId, int roleId, int userId) {
        
        checkAdminRole(roleId);
        
        //Pallet must exist
        Pallet pallet = findByIdOrThrow(palletRepository, palletId, PalletErrorCode.PALLET_NOT_FOUND);
        
        //Pallet status must be CREATING 
        if (pallet.getStatus() != PalletStatus.CREATING) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }
        
        //Order must exist
        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));
        
        //Order must belong route of pallet
        if (order.getRouteConfig() == null || pallet.getRouteConfig() == null ||
                !order.getRouteConfig().getRouteId().equals(pallet.getRouteConfig().getRouteId())) {
            throw new AppException(PalletErrorCode.ROUTE_MISMATCH);
        }
        
        //Order status must NEW
        if (order.getStatus() != OrderStatus.NEW) {
            throw new AppException(PalletErrorCode.INVALID_ORDER_STATUS);
        }

        //Re calculate total weight and volume of this pallet
        BigDecimal newTotalWeight = pallet.getTotalWeightKg().add(order.getTotalWeightKg());
        BigDecimal newTotalVolume = pallet.getTotalVolumeM3().add(order.getTotalVolumeM3());

        RouteConfig routeConfig = pallet.getRouteConfig();
        //Check constraint vehicle: prioritize linehaulTrip's vehicle, fallback to default vehicle of route config
        Vehicle vehicle = null;
        boolean hasLinehaulTripVehicle = false;

        if (pallet.getLinehaulTrip() != null) {
            LinehaulTrip linehaulTrip = pallet.getLinehaulTrip();
            if (linehaulTrip.getVehicle() != null) {
                vehicle = linehaulTrip.getVehicle();
                hasLinehaulTripVehicle = true;
            } else if (linehaulTrip.getRouteConfig() != null && linehaulTrip.getRouteConfig().getDefaultVehicle() != null) {
                vehicle = linehaulTrip.getRouteConfig().getDefaultVehicle();
                hasLinehaulTripVehicle = true;
            }
        }

        if (!hasLinehaulTripVehicle && routeConfig != null && routeConfig.getDefaultVehicle() != null) {
            vehicle = routeConfig.getDefaultVehicle();
        }

        if (vehicle != null) {
            if (hasLinehaulTripVehicle) {
                LinehaulTrip linehaulTrip = pallet.getLinehaulTrip();
                BigDecimal currentTripWeight = linehaulTrip.getPallets().stream()
                        .map(Pallet::getTotalWeightKg)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                BigDecimal newTripWeight = currentTripWeight.add(order.getTotalWeightKg());

                BigDecimal currentTripVolume = linehaulTrip.getPallets().stream()
                        .map(Pallet::getTotalVolumeM3)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                BigDecimal newTripVolume = currentTripVolume.add(order.getTotalVolumeM3());

                if (vehicle.getMaxWeightKg() != null && newTripWeight.compareTo(vehicle.getMaxWeightKg()) > 0) {
                    throw new AppException(PalletErrorCode.CAPACITY_EXCEEDED);
                }
                if (vehicle.getMaxVolumeM3() != null && newTripVolume.compareTo(vehicle.getMaxVolumeM3()) > 0) {
                    throw new AppException(PalletErrorCode.CAPACITY_EXCEEDED);
                }
            } else {
                if (vehicle.getMaxWeightKg() != null && newTotalWeight.compareTo(vehicle.getMaxWeightKg()) > 0) {
                    throw new AppException(PalletErrorCode.CAPACITY_EXCEEDED);
                }
                if (vehicle.getMaxVolumeM3() != null && newTotalVolume.compareTo(vehicle.getMaxVolumeM3()) > 0) {
                    throw new AppException(PalletErrorCode.CAPACITY_EXCEEDED);
                }
            }
        }

        PalletItem palletItem = new PalletItem();
        palletItem.setPallet(pallet);
        palletItem.setOrder(order);
        pallet.getPalletItems().add(palletItem);
        pallet.setTotalWeightKg(newTotalWeight);
        pallet.setTotalVolumeM3(newTotalVolume);

        order.setStatus(OrderStatus.READY_TO_PICK);
        orderRepository.save(order);

        palletRepository.save(pallet);

        return palletItemMapper.toResponse(palletItem);
    }

    @Override
    @Transactional
    public PalletItemResponseDTO scanPalletItem( Long palletId, String orderCode, int roleId, int userId) {
        checkStaffOrAdminRole(roleId);

        //Pallet must exist
        Pallet pallet = palletRepository.findByIdWithItemsAndOrders(palletId)
                .orElseThrow(() -> new AppException(PalletErrorCode.PALLET_NOT_FOUND));

        //Pallet status must be CAN_SEAL
        if (pallet.getStatus() != PalletStatus.CAN_SEAL) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        //Pallet item must exist
        PalletItem itemToScan = pallet.getPalletItems().stream()
                .filter(item -> item.getOrder() != null && orderCode.equals(item.getOrder().getOrderCode()))
                .findFirst()
                .orElseThrow(() -> new AppException(PalletErrorCode.ORDER_MISMATCH));

        //Order status must be READY TO PICK
        Order order = itemToScan.getOrder();
        if (!OrderStatus.READY_TO_PICK.equals(order.getStatus())) {
            throw new AppException(PalletErrorCode.ORDER_ALREADY_SCANNED);
        }

        //Update status order
        order.setStatus(OrderStatus.IN_PALLET);
        orderRepository.save(order);

        //Update make item is scanned
        itemToScan.setIsScanned(true);
        itemToScan.setScannedAt(LocalDateTime.now());

        palletRepository.save(pallet);

        return palletItemMapper.toResponse(itemToScan);
    }

    @Override
    @Transactional
    public void removePalletItem(Long palletId, String orderCode, int roleId, int userId) {

        //Pallet must exist
        Pallet pallet = findByIdOrThrow(palletRepository, palletId, PalletErrorCode.PALLET_NOT_FOUND);

        //Pallet status must be CREATING
        if (pallet.getStatus() != PalletStatus.CREATING) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        //Pallet item must exist
        PalletItem itemToRemove = pallet.getPalletItems().stream()
                .filter(item -> item.getOrder() != null && orderCode.equals(item.getOrder().getOrderCode()))
                .findFirst()
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

        Order order = itemToRemove.getOrder();

        pallet.getPalletItems().remove(itemToRemove);

        if (order != null) {
            order.setStatus(OrderStatus.NEW);
            orderRepository.save(order);
        }

       BigDecimal totalWeight = pallet.getPalletItems().stream()
                .map(item -> item.getOrder() != null ? item.getOrder().getTotalWeightKg() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        BigDecimal totalVolume = pallet.getPalletItems().stream()
                .map(item -> item.getOrder() != null ? item.getOrder().getTotalVolumeM3() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        pallet.setTotalWeightKg(totalWeight);
        pallet.setTotalVolumeM3(totalVolume);

        palletRepository.save(pallet);
    }

    @Override
    @Transactional
    public PalletResponseDTO makeSealed(Long palletId, int roleId, int userId){
        checkStaffOrAdminRole(roleId);

        Pallet pallet = findByIdOrThrow(palletRepository, palletId, PalletErrorCode.PALLET_NOT_FOUND);

        if (pallet.getStatus() != PalletStatus.CAN_SEAL) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        if(pallet.getPalletItems().isEmpty()){
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        boolean allScanned = pallet.getPalletItems().stream()
                .allMatch(item -> Boolean.TRUE.equals(item.getIsScanned()));
        if (!allScanned) {
            throw new AppException(PalletErrorCode.PALLET_MISSING_PALLET_ITEM);
        }

        pallet.setStatus(PalletStatus.SEALED);
        palletRepository.save(pallet);

        return palletMapper.toResponse(pallet);
    }

    private double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    //To employee scan barcode to confirm received pallet
    @Override
    @Transactional
    public PalletResponseDTO confirmPalletArrival(String palletCode, Double latitude, Double longitude, int roleId, int userId) {
        Pallet pallet = palletRepository.findByPalletCode(palletCode)
                .orElseThrow(() -> new AppException(PalletErrorCode.PALLET_NOT_FOUND));

        if (!PalletStatus.IN_TRANSIT.equals(pallet.getStatus())) {
            throw new AppException(PalletErrorCode.PALLET_NOT_IN_TRANSIT);
        }

        RouteConfig routeConfig = pallet.getRouteConfig();
        if (routeConfig == null || routeConfig.getToWarehouse() == null) {
            throw new AppException(RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);
        }

        Warehouse toWarehouse = routeConfig.getToWarehouse();
        if (toWarehouse.getLocation() == null) {
            throw new AppException(WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        }

        double lat1 = toWarehouse.getLocation().getY();
        double lon1 = toWarehouse.getLocation().getX();
        double distance = calculateDistanceInMeters(lat1, lon1, latitude, longitude);
        if (distance > 500.0) {
            throw new AppException(LinehaulTripErrorCode.GPS_NOT_NEAR_TO_WAREHOUSE);
        }

        pallet.setStatus(PalletStatus.ARRIVED);

//        for (PalletItem item : pallet.getPalletItems()) {
//            Order order = item.getOrder();
//            if (order != null && OrderStatus.IN_TRANSIT_LINEHAUL.equals(order.getStatus())) {
//                order.setStatus(OrderStatus.ARRIVED_AT_HUB);
//                orderRepository.save(order);
//            }
//        }

        Pallet saved = palletRepository.save(pallet);
        return palletMapper.toResponse(saved);
    }

    //To employee scan barcode to confirm received order
    @Override
    @Transactional
    public void confirmOrderArrival(String orderCode, Double latitude, Double longitude, int roleId, int userId) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!OrderStatus.IN_TRANSIT_LINEHAUL.equals(order.getStatus())) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        RouteConfig routeConfig = order.getRouteConfig();
        if (routeConfig == null || routeConfig.getToWarehouse() == null) {
            throw new AppException(RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);
        }

        Warehouse toWarehouse = routeConfig.getToWarehouse();
        if (toWarehouse.getLocation() == null) {
            throw new AppException(WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        }

        double lat1 = toWarehouse.getLocation().getY();
        double lon1 = toWarehouse.getLocation().getX();
        double distance = calculateDistanceInMeters(lat1, lon1, latitude, longitude);
        if (distance > 500.0) {
            throw new AppException(LinehaulTripErrorCode.GPS_NOT_NEAR_TO_WAREHOUSE);
        }

        order.setStatus(OrderStatus.ARRIVED_AT_HUB);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public PalletResponseDTO updateStatusToCanSeal(Long palletId, int roleId, int userId) {
        checkStaffOrAdminRole(roleId);
        Pallet pallet = findByIdOrThrow(palletRepository, palletId, PalletErrorCode.PALLET_NOT_FOUND);

        if (pallet.getStatus() != PalletStatus.CREATING) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        if (pallet.getPalletItems().isEmpty()) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        pallet.setStatus(PalletStatus.CAN_SEAL);
        Pallet saved = palletRepository.save(pallet);

        // Pallet giờ đã sẵn sàng để quét đơn -> thông báo cho toàn bộ staff đang hoạt động.
        notifyStaffPalletReadyToScan(saved.getPalletCode());

        return palletMapper.toResponse(saved);
    }

    /**
     * Gửi thông báo "pallet sẵn sàng để quét" tới mọi STAFF đang active.
     * Nhiệm vụ pallet là hàng đợi toàn cục (không gắn kho/nhân sự cụ thể) nên
     * broadcast cho tất cả staff; mỗi staff nhận realtime qua topic riêng của họ.
     * Lỗi gửi thông báo không được làm hỏng thao tác chuyển trạng thái pallet.
     */
    private void notifyStaffPalletReadyToScan(String palletCode) {
        if (palletCode == null) {
            return;
        }
        try {
            List<User> staffs = userRepository.findByRole_RoleIdInAndIsActiveTrue(List.of(STAFF_ROLE_ID));
            for (User staff : staffs) {
                if (staff.getUserId() != null) {
                    notificationRealtimeService.sendPalletizationTaskNotification(staff.getUserId(), palletCode);
                }
            }
        } catch (Exception ex) {
            // Không chặn nghiệp vụ chính nếu kênh thông báo lỗi.
            org.slf4j.LoggerFactory.getLogger(PalletServiceImpl.class)
                    .warn("Failed to notify staff for pallet {}: {}", palletCode, ex.getMessage());
        }
    }
}
