package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.PalletUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.PalletResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import com.overcode250204.smartlogicticssystem.exception.*;
import com.overcode250204.smartlogicticssystem.mapper.PalletMapper;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletRepository;
import com.overcode250204.smartlogicticssystem.services.IPalletService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import com.overcode250204.smartlogicticssystem.utils.BarcodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

//NOTE =============CRUD PALLET to ADMIN manage manual=========================
@Service
@RequiredArgsConstructor
public class PalletServiceImpl extends BaseServiceImpl implements IPalletService {

    private final S3FileService s3FileService;
    private final PalletRepository palletRepository;
    private final LinehaulTripRepository linehaulTripRepository;
    private final OrderRepository orderRepository;
    private final PalletMapper palletMapper;

    private void checkAdminRole(int roleId) {
        if (roleId != 1) {
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

        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, request.getLinehaulTripId(), LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);
        if (linehaulTrip.getRouteConfig() == null || linehaulTrip.getVehicle() == null) {
            throw new AppException(PalletErrorCode.LINEHAUL_TRIP_MISSING_CONFIG);
        }

        Set<Order> orders = new HashSet<>();
        BigDecimal newWeight = BigDecimal.ZERO;
        BigDecimal newVolume = BigDecimal.ZERO;

        if (request.getOrderIds() != null && !request.getOrderIds().isEmpty()) {
            for (Long orderId : request.getOrderIds()) {
                Order order = findByIdOrThrow(orderRepository, orderId, OrderErrorCode.ORDER_NOT_FOUND);

                if (order.getStatus() != OrderStatus.NEW) {
                    throw new AppException(PalletErrorCode.INVALID_ORDER_STATUS);
                }

                if (order.getRouteConfig() == null || !order.getRouteConfig().getRouteId().equals(linehaulTrip.getRouteConfig().getRouteId())) {
                    throw new AppException(PalletErrorCode.ROUTE_MISMATCH);
                }

                orders.add(order);
                newWeight = newWeight.add(order.getTotalWeightKg());
                newVolume = newVolume.add(order.getTotalVolumeM3());
            }
        }

        // Capacity validation
        List<Pallet> existingPallets = palletRepository.findPalletByLinehaulTrip(linehaulTrip);
        BigDecimal existingWeight = BigDecimal.ZERO;
        BigDecimal existingVolume = BigDecimal.ZERO;
        for (Pallet p : existingPallets) {
            if (p.getOrders() != null) {
                for (Order o : p.getOrders()) {
                    existingWeight = existingWeight.add(o.getTotalWeightKg());
                    existingVolume = existingVolume.add(o.getTotalVolumeM3());
                }
            }
        }

        BigDecimal totalWeight = existingWeight.add(newWeight);
        BigDecimal totalVolume = existingVolume.add(newVolume);

        if (totalWeight.compareTo(linehaulTrip.getVehicle().getMaxWeightKg()) > 0 ||
                totalVolume.compareTo(linehaulTrip.getVehicle().getMaxVolumeM3()) > 0) {
            throw new AppException(PalletErrorCode.CAPACITY_EXCEEDED);
        }

        // Change status of orders to READY_TO_PICK
        for (Order o : orders) {
            o.setStatus(OrderStatus.READY_TO_PICK);
            orderRepository.save(o);
        }

        Pallet pallet = new Pallet();
        pallet.setLinehaulTrip(linehaulTrip);
        pallet.setStatus(PalletStatus.CREATING);
        pallet.setOrders(orders);

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
    @Transactional
    public PalletResponseDTO update(Long id, PalletUpdateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);

        Pallet pallet = findByIdOrThrow(palletRepository, id, PalletErrorCode.PALLET_NOT_FOUND);
        if (pallet.getStatus() != PalletStatus.CREATING) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        LinehaulTrip targetTrip = findByIdOrThrow(linehaulTripRepository, request.getLinehaulTripId(), LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);
        if (targetTrip.getRouteConfig() == null || targetTrip.getVehicle() == null) {
            throw new AppException(PalletErrorCode.LINEHAUL_TRIP_MISSING_CONFIG);
        }

        Set<Order> currentOrders = pallet.getOrders() != null ? pallet.getOrders() : new HashSet<>();
        List<Long> targetOrderIds = request.getOrderIds() != null ? request.getOrderIds() : new ArrayList<>();

        Set<Order> targetOrders = new HashSet<>();
        List<Order> ordersToRevert = new ArrayList<>();
        List<Order> ordersToPick = new ArrayList<>();

        BigDecimal newWeight = BigDecimal.ZERO;
        BigDecimal newVolume = BigDecimal.ZERO;

        for (Long orderId : targetOrderIds) {
            Order order = findByIdOrThrow(orderRepository, orderId, OrderErrorCode.ORDER_NOT_FOUND);

            // If order is newly added to this pallet
            if (!currentOrders.contains(order)) {
                if (order.getStatus() != OrderStatus.NEW) {
                    throw new AppException(PalletErrorCode.INVALID_ORDER_STATUS);
                }
                ordersToPick.add(order);
            }

            if (order.getRouteConfig() == null || !order.getRouteConfig().getRouteId().equals(targetTrip.getRouteConfig().getRouteId())) {
                throw new AppException(PalletErrorCode.ROUTE_MISMATCH,
                     "Order " +
                      order.getOrderCode() + 
                      " is not in the same route as the target trip");
            }

            targetOrders.add(order);
            newWeight = newWeight.add(order.getTotalWeightKg());
            newVolume = newVolume.add(order.getTotalVolumeM3());
        }

        // Identify removed orders to revert status to NEW
        for (Order o : currentOrders) {
            if (!targetOrders.contains(o)) {
                ordersToRevert.add(o);
            }
        }

        // Capacity validation
        List<Pallet> existingPalletsOfTargetTrip = palletRepository.findPalletByLinehaulTrip(targetTrip);
        BigDecimal otherPalletsWeight = BigDecimal.ZERO;
        BigDecimal otherPalletsVolume = BigDecimal.ZERO;
        for (Pallet p : existingPalletsOfTargetTrip) {
            if (!p.getPalletId().equals(pallet.getPalletId()) && p.getOrders() != null) {
                for (Order o : p.getOrders()) {
                    otherPalletsWeight = otherPalletsWeight.add(o.getTotalWeightKg());
                    otherPalletsVolume = otherPalletsVolume.add(o.getTotalVolumeM3());
                }
            }
        }

        BigDecimal totalWeight = otherPalletsWeight.add(newWeight);
        BigDecimal totalVolume = otherPalletsVolume.add(newVolume);

        if (totalWeight.compareTo(targetTrip.getVehicle().getMaxWeightKg()) > 0 ||
                totalVolume.compareTo(targetTrip.getVehicle().getMaxVolumeM3()) > 0) {
            throw new AppException(PalletErrorCode.CAPACITY_EXCEEDED);
        }

        // Apply state modifications
        for (Order o : ordersToRevert) {
            o.setStatus(OrderStatus.NEW);
            orderRepository.save(o);
        }

        for (Order o : ordersToPick) {
            o.setStatus(OrderStatus.READY_TO_PICK);
            orderRepository.save(o);
        }

        pallet.setLinehaulTrip(targetTrip);
        pallet.setOrders(targetOrders);
        pallet.setStatus(request.getStatus());

        Pallet saved = palletRepository.save(pallet);
        return palletMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        checkAdminRole(roleId);

        Pallet pallet = findByIdOrThrow(palletRepository, id, PalletErrorCode.PALLET_NOT_FOUND);
        if (pallet.getStatus() != PalletStatus.CREATING && pallet.getStatus() != PalletStatus.SEALED) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_DELETE);
        }

        if (pallet.getOrders() != null) {
            for (Order o : pallet.getOrders()) {
                o.setStatus(OrderStatus.NEW);
                orderRepository.save(o);
            }
        }

        palletRepository.delete(pallet);
    }
}
