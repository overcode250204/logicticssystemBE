package com.overcode250204.smartlogicticssystem.services.impls;


import com.overcode250204.smartlogicticssystem.dtos.request.delivery.IncidentReportDTO;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.repositories.*;
import com.overcode250204.smartlogicticssystem.routing.RoutingEngine;
import com.overcode250204.smartlogicticssystem.routing.domain.DepotSetting;
import com.overcode250204.smartlogicticssystem.routing.domain.Driver;
import com.overcode250204.smartlogicticssystem.routing.domain.OrderPlaning;
import com.overcode250204.smartlogicticssystem.routing.domain.RoutePlanSolution;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderBatchRepository batchRepository;
    private final RoutePointRepository routePointRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final ZoneRepository zoneRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final RoutingEngine routingEngine;


    /**
     * WORKFLOW 2: HỆ THỐNG GOM ĐƠN TỐI ƯU -> TRỪ KHO THỰC TẾ KHI XÁC NHẬN ĐÓNG GÓI
     */
    @Transactional
    public OrderBatch createOptimizedBatch(Long shipperId, List<Long> orderedOrderIds) {


        DriverProfile driverProfile = driverProfileRepository.findById(shipperId).orElseThrow(() -> new RuntimeException("Shipper does not exist!"));

        OrderBatch batch = new OrderBatch();
        batch.setDriverProfile(driverProfile);
        batch.setStatus("ASSIGNED");
//        batch.setOptimizedAt(LocalDateTime.now());

        batch = batchRepository.save(batch);

        int sequence = 1;
        for (Long orderId : orderedOrderIds) {
            Order order = orderRepository.findById(orderId).orElseThrow();

            // Chuyển sang ĐÃ XÁC NHẬN để thủ kho thấy và đóng gói
            order.setStatus("CONFIRMED");
            orderRepository.save(order);

            // Nghiệp vụ trừ kho thực tế diễn ra TẠI ĐÂY (Khi đơn chính thức được chấp nhận điều phối)
//            for (OrderItem item : order.getItems()) {
//                Product product = item.getProduct();
//                if (product.getQ() < item.getQuantity()) {
//                    throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ số lượng trong kho thực tế!");
//                }
//                product.setPhysicalQty(product.getPhysicalQty() - item.getQuantity());
//                productRepository.save(product);
//            }

            // Tạo điểm dừng trong lộ trình cho tài xế
            RoutePoint routePoint = RoutePoint.builder()
                    .batch(batch)
                    .order(order)
                    .sequenceNumber(sequence++)
                    .status("TODO")
                    .build();
            routePointRepository.save(routePoint);
        }

        return batch;
    }

    /**
     * WORKFLOW 3: SHIPPER XUẤT KHO -> ĐANG GIAO (BẬT LIVE TRACKING)
     */
    @Transactional
    public void checkInAndStartDelivery(Long batchId) {
        OrderBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Delivery Ship dose not exist.!"));

        batch.setStatus("PICKED_UP");
        batchRepository.save(batch);

        for (RoutePoint rp : batch.getRoutePoints()) {
            Order order = rp.getOrder();

            saveDeliveryLog(order, batch.getDriverProfile(), order.getStatus(), "IN_TRANSIT", "STATUS_CHANGE", "Shipper đã lấy hàng.", "not image");

            order.setStatus("IN_TRANSIT");
            orderRepository.save(order);
        }
    }

    /**
     * WORKFLOW 4: SHIPPER CẬP NHẬT TIẾN ĐỘ TỪNG ĐƠN (ĐÃ GIAO / GIAO LỖI)
     */
    @Transactional
    public void updateRoutePointStatus(Long routePointId, String targetStatus, IncidentReportDTO incident) {
        RoutePoint rp = routePointRepository.findById(routePointId).orElseThrow();
        Order order = rp.getOrder();
        DriverProfile driverProfile = rp.getBatch().getDriverProfile();
        String previousStatus = order.getStatus();

        if ("ARRIVED".equals(targetStatus)) {
//            rp.setActualArrivalTime(LocalDateTime.now());
            rp.setStatus("ARRIVED");
        }
        else if ("DELIVERED".equals(targetStatus)) {
            rp.setStatus("COMPLETED");
            order.setStatus("DELIVERED");
            saveDeliveryLog(order, driverProfile, previousStatus, "DELIVERED", "STATUS_CHANGE", "Giao hàng thành công.", "");
        }
        else if ("FAILED".equals(targetStatus)) {
            rp.setStatus("FAILED");
            order.setStatus("FAILED");

            // Giao thất bại -> Lưu lý do phát sinh của shipper
            saveDeliveryLog(order, driverProfile, previousStatus, "FAILED", "INCIDENT", incident.getNote(), incident.getImageUrl());

            // TÙY CHỌN NGHIỆP VỤ: Nếu giao thất bại, hàng phải hoàn về kho,  cộng lại kho thực tế ở đây
            // Hoặc đợi khi shipper cầm hàng về đến kho, thủ kho bấm "Xác nhận hoàn hàng" thì mới cộng lại kho sau.
        }

        orderRepository.save(order);
        routePointRepository.save(rp);
    }

    private void saveDeliveryLog(Order order, DriverProfile driverProfile, String fromStatus, String toStatus, String type, String note, String imageUrl  ) {
        DeliveryLog log = DeliveryLog.builder()
                .order(order)
                .driverProfile(driverProfile)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .logType(type)
                .note(note)
                .imageUrl(imageUrl)
                .build();
        deliveryLogRepository.save(log);
    }

    public RoutePlanSolution executeRouting(){
        DepotSetting depot = DepotSetting.builder()
                .id(1L)
                .location(geometryFactory.createPoint(new Coordinate( 106.80071256657389,10.875405129142845)))
                .build();
        List<DriverProfile> driverProfiles = driverProfileRepository.findAll();
        List<Driver> drivers = driverProfiles.stream().map((x) -> Driver.builder().id(x.getId()).maxWeightCapacity(x.getMaxWeightCapacity()).depot(depot).orderPlaningList(new ArrayList<>()).build()).toList();
        List<Order> ordersE = orderRepository.findAll();
        List<OrderPlaning> orderPlannings = ordersE.stream().map((x) -> OrderPlaning.builder().id(x.getId()).destination(x.getDeliveryLocation()).weightKg(x.getTotalWeight().doubleValue()).build()).toList();
        RoutePlanSolution solvedSolution = routingEngine.executeRouting(depot,drivers, orderPlannings);

        // lấy data từ solver để lưu db
        for (Driver driver : solvedSolution.getDriverList()) {
            List<OrderPlaning> assignedOrderPlanings = driver.getOrderPlaningList();

            if (assignedOrderPlanings.isEmpty()) {
                continue;
            }

            DriverProfile dp = driverProfileRepository.findById(driver.getId()).orElseThrow(() -> new RuntimeException("Driver profile does not exist!"));
            OrderBatch orderBatch = OrderBatch.builder()
                    .driverProfile(dp)
                    .routePoints(new ArrayList<>())
                    .totalWeight(new BigDecimal(0))
                    .build();

            for (int index = 0; index < assignedOrderPlanings.size(); index++) {
                OrderPlaning orderPlaning = assignedOrderPlanings.get(index);

//                System.out.printf("Thứ tự dừng [%d]: Đơn hàng ID %d (Trọng lượng: %.2f kg)\n",
//                        index + 1, orderPlaning.getId(), orderPlaning.getWeightKg());
                Order order = orderRepository.findById(orderPlaning.getId()).orElseThrow();
                int sequence = index + 1;
                RoutePoint point = RoutePoint.builder()
                        .order(order)
                        .batch(orderBatch)
                        .sequenceNumber(sequence)
                        .build();
                BigDecimal totalWeightKg = orderBatch.getTotalWeight().add(new BigDecimal(orderPlaning.getWeightKg()));
                orderBatch.setTotalWeight(totalWeightKg);
                orderBatch.getRoutePoints().add(point);
            }

            orderBatch = batchRepository.save(orderBatch);

        }

        return null;
    }


}
