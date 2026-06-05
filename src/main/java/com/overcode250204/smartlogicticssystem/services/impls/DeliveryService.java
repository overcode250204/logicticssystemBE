package com.overcode250204.smartlogicticssystem.services.impls;


import com.overcode250204.smartlogicticssystem.dtos.request.delivery.IncidentReportDTO;
import com.overcode250204.smartlogicticssystem.dtos.request.delivery.OrderItemDTO;
import com.overcode250204.smartlogicticssystem.dtos.request.delivery.OrderRequestDTO;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final ShipperRepository shipperRepository;
    private final ZoneRepository zoneRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * WORKFLOW 1: KHÁCH HÀNG BẤM ĐẶT HÀNG -> TẠO ĐƠN PENDING
     */
    @Transactional
    public OrderRequestDTO placeOrder(OrderRequestDTO request) {

        //Kiểm tra customer
        User customer = userRepository.findById(request.getCustomerId()).orElseThrow(() -> new RuntimeException("User does not exist!"));

        // Chuyển đổi tọa độ GPS từ Flutter gửi lên
        Point customerLocation = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));

        Zone zone = zoneRepository.findAreaContainingPoint(request.getLongitude(), request.getLatitude()).orElseThrow(() -> new RuntimeException("Not support this address!"));


        // Tạo nhanh đơn hàng ở trạng thái PENDING
        Order order = Order.builder()
                .customer(customer)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryLocation(customerLocation)
                .status("PENDING")
                .zone(zone)
                .totalAmount(new BigDecimal("000.00"))
                .items(new ArrayList<>())
                .build();

        // Lưu chi tiết các sản phẩm trong đơn hàng
        for (OrderItemDTO itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product does not exist!"));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.getItems().add(orderItem);
            order.setTotalAmount(order.getTotalAmount().add(calculateTotalAmount(orderItem.getPrice(), orderItem.getQuantity())));
        }
        order = orderRepository.save(order);
        return request;
    }

    private BigDecimal calculateTotalAmount(BigDecimal price, Integer quantity){
        BigDecimal quantityBig = BigDecimal.valueOf(quantity);
        return price.multiply(quantityBig);
    }

    /**
     * WORKFLOW 2: HỆ THỐNG GOM ĐƠN TỐI ƯU -> TRỪ KHO THỰC TẾ KHI XÁC NHẬN ĐÓNG GÓI
     */
    @Transactional
    public OrderBatch createOptimizedBatch(Long shipperId, List<Long> orderedOrderIds) {


        Shipper shipper = shipperRepository.findById(shipperId).orElseThrow(() -> new RuntimeException("Shipper does not exist!"));

        OrderBatch batch = new OrderBatch();
        batch.setShipper(shipper);
        batch.setStatus("ASSIGNED");
        batch.setOptimizedAt(LocalDateTime.now());

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

            saveDeliveryLog(order, batch.getShipper(), order.getStatus(), "IN_TRANSIT", "STATUS_CHANGE", "Shipper đã lấy hàng.", "not image");

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
        Shipper shipper = rp.getBatch().getShipper();
        String previousStatus = order.getStatus();

        if ("ARRIVED".equals(targetStatus)) {
            rp.setActualArrivalTime(LocalDateTime.now());
            rp.setStatus("ARRIVED");
        }
        else if ("DELIVERED".equals(targetStatus)) {
            rp.setStatus("COMPLETED");
            order.setStatus("DELIVERED");
            saveDeliveryLog(order, shipper, previousStatus, "DELIVERED", "STATUS_CHANGE", "Giao hàng thành công.", "");
        }
        else if ("FAILED".equals(targetStatus)) {
            rp.setStatus("FAILED");
            order.setStatus("FAILED");

            // Giao thất bại -> Lưu lý do phát sinh của shipper
            saveDeliveryLog(order, shipper, previousStatus, "FAILED", "INCIDENT", incident.getNote(), incident.getImageUrl());

            // TÙY CHỌN NGHIỆP VỤ: Nếu giao thất bại, hàng phải hoàn về kho,  cộng lại kho thực tế ở đây
            // Hoặc đợi khi shipper cầm hàng về đến kho, thủ kho bấm "Xác nhận hoàn hàng" thì mới cộng lại kho sau.
        }

        orderRepository.save(order);
        routePointRepository.save(rp);
    }

    private void saveDeliveryLog(Order order, Shipper shipper, String fromStatus, String toStatus, String type, String note, String imageUrl  ) {
        DeliveryLog log = DeliveryLog.builder()
                .order(order)
                .shipper(shipper)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .logType(type)
                .note(note)
                .imageUrl(imageUrl)
                .build();
        deliveryLogRepository.save(log);
    }
}
