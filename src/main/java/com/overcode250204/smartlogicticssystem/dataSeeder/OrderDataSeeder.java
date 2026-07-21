package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.ExceptionReason;
import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.entities.OrderException;
import com.overcode250204.smartlogicticssystem.entities.OrderItem;
import com.overcode250204.smartlogicticssystem.entities.OrderTracking;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.entities.Zone;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.enums.PaymentType;
import com.overcode250204.smartlogicticssystem.repositories.ExceptionReasonRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderExceptionRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderItemRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderTrackingRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.repositories.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDataSeeder implements DataSeeder {

    private static final int SRID_WGS_84 = 4326;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTrackingRepository orderTrackingRepository;
    private final OrderExceptionRepository orderExceptionRepository;
    private final ExceptionReasonRepository exceptionReasonRepository;
    private final ProductRepository productRepository;
    private final RouteConfigRepository routeConfigRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public int getOrder() {
        return 16;
    }

    @Override
    @Transactional
    public void seed() {
        LocalDateTime now = LocalDateTime.now();
        List<OrderSeed> orders = List.of(
                order("ORD-SEED-000001", OrderStatus.NEW, "trandinhbao222@gmail.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.CENTRAL_HCM_ZONE,
                        "12 Nguyen Hue, District 1", "Ho Chi Minh", 106.7037, 10.7753, now.minusHours(3), 1),
                order("ORD-SEED-000002", OrderStatus.READY_TO_PICK, "customer2@logistics.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.EAST_HCM_ZONE,
                        "80 Vo Nguyen Giap, Thu Duc", "Ho Chi Minh", 106.7582, 10.8421, now.minusHours(8), 2),
                order("ORD-SEED-000003", OrderStatus.IN_PALLET, "customer3@logistics.com",
                        RouteConfigDataSeeder.DONG_NAI_ROUTE, WarehouseZoneDataSeeder.BIEN_HOA_ZONE,
                        "18 Pham Van Thuan, Bien Hoa", "Dong Nai", 106.8245, 10.9452, now.minusDays(1), 3),
                order("ORD-SEED-000004", OrderStatus.IN_TRANSIT_LINEHAUL, "customer2@logistics.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.EAST_HCM_ZONE,
                        "23 Le Van Viet, Thu Duc", "Ho Chi Minh", 106.7761, 10.8448, now.minusDays(2), 4),
                order("ORD-SEED-000005", OrderStatus.ARRIVED_AT_HUB, "customer3@logistics.com",
                        RouteConfigDataSeeder.DONG_NAI_ROUTE, WarehouseZoneDataSeeder.BIEN_HOA_ZONE,
                        "30 Dong Khoi, Bien Hoa", "Dong Nai", 106.8322, 10.9475, now.minusDays(3), 5),
                order("ORD-SEED-000006", OrderStatus.IN_TRANSIT_LOCAL, "trandinhbao222@gmail.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.SOUTH_HCM_ZONE,
                        "45 Nguyen Van Linh, District 7", "Ho Chi Minh", 106.7216, 10.7299, now.minusDays(4), 6),
                order("ORD-SEED-000007", OrderStatus.ARRIVED_AT_DELIVERY_POINT, "customer2@logistics.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.CENTRAL_HCM_ZONE,
                        "68 Hai Ba Trung, District 1", "Ho Chi Minh", 106.7044, 10.7810, now.minusDays(5), 7),
                order("ORD-SEED-000008", OrderStatus.DELIVERED, "customer3@logistics.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.CENTRAL_HCM_ZONE,
                        "22 Le Loi, District 1", "Ho Chi Minh", 106.7009, 10.7738, now.minusDays(7), 8),
                order("ORD-SEED-000009", OrderStatus.FAILED, "trandinhbao222@gmail.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.SOUTH_HCM_ZONE,
                        "90 Huynh Tan Phat, District 7", "Ho Chi Minh", 106.7314, 10.7338, now.minusDays(6), 9),
                order("ORD-SEED-000010", OrderStatus.CANCELLED, "customer2@logistics.com",
                        RouteConfigDataSeeder.DONG_NAI_ROUTE, WarehouseZoneDataSeeder.BIEN_HOA_ZONE,
                        "55 Vo Thi Sau, Bien Hoa", "Dong Nai", 106.8350, 10.9500, now.minusDays(8), 10),
                order("ORD-SEED-000011", OrderStatus.IN_PALLET, "trandinhbao222@gmail.com",
                        RouteConfigDataSeeder.HCM_ROUTE, WarehouseZoneDataSeeder.SOUTH_HCM_ZONE,
                        "16 Nguyen Thi Thap, District 7", "Ho Chi Minh", 106.7150, 10.7380, now.minusDays(1), 11)
        );

        orders.forEach(this::createOrderAggregate);
        log.info("Order, order-item, tracking, and exception seed data completed.");
    }

    private OrderSeed order(
            String code,
            OrderStatus status,
            String customerEmail,
            String routeName,
            String zoneName,
            String address,
            String province,
            double longitude,
            double latitude,
            LocalDateTime createdAt,
            int sequence
    ) {
        return new OrderSeed(
                code, status, customerEmail, routeName, zoneName, address, province,
                longitude, latitude, createdAt,
                sequence % 2 == 0 ? PaymentType.CREDIT : PaymentType.COD,
                List.of(
                        new ItemSeed("PRD-FOOD-001", 1 + sequence % 3),
                        new ItemSeed(sequence % 2 == 0 ? "PRD-BEV-002" : "PRD-HOUSE-001", 1 + sequence % 2)
                )
        );
    }

    private void createOrderAggregate(OrderSeed seed) {
        User customer = userRepository.findByEmail(seed.customerEmail())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + seed.customerEmail()));
        RouteConfig route = routeConfigRepository.findByRouteNameIgnoreCase(seed.routeName())
                .orElseThrow(() -> new IllegalStateException("Route not found: " + seed.routeName()));
        Zone zone = zoneRepository.findByNameIgnoreCase(seed.zoneName())
                .orElseThrow(() -> new IllegalStateException("Zone not found: " + seed.zoneName()));
        List<ResolvedItem> resolvedItems = seed.items().stream()
                .map(this::resolveItem)
                .toList();

        Order order = orderRepository.findByOrderCode(seed.code())
                .orElseGet(() -> createOrder(seed, customer, route, zone, resolvedItems));

        if (orderItemRepository.findByOrderIn(List.of(order)).isEmpty()) {
            List<OrderItem> items = resolvedItems.stream()
                    .map(item -> createOrderItem(order, item, seed.status()))
                    .toList();
            orderItemRepository.saveAll(items);
        }

        createTrackingHistory(order, seed);
        if (seed.status() == OrderStatus.FAILED) {
            createOrderException(order);
        }
    }

    private Order createOrder(
            OrderSeed seed,
            User customer,
            RouteConfig route,
            Zone zone,
            List<ResolvedItem> items
    ) {
        BigDecimal totalAmount = items.stream()
                .map(item -> item.product().getPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalWeight = items.stream()
                .map(item -> item.product().getWeight().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalVolume = items.stream()
                .map(item -> item.volumeM3().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderCode(seed.code());
        order.setBarcodeUrl("seed://order/" + seed.code());
        order.setCustomer(customer);
        order.setCustomerName(customer.getFullName());
        order.setPhone(customer.getPhone());
        order.setDeliveryAddress(seed.deliveryAddress());
        order.setDeliveryProvince(seed.deliveryProvince());
        order.setDeliveryPoint(point(seed.longitude(), seed.latitude()));
        order.setAssignedHub(route.getToWarehouse());
        order.setRouteConfig(route);
        order.setZone(zone);
        order.setPaymentType(seed.paymentType());
        order.setStatus(seed.status());
        order.setTotalAmount(totalAmount);
        order.setTotalWeightKg(totalWeight);
        order.setTotalVolumeM3(totalVolume);
        order.setCreatedAt(seed.createdAt());
        order.setEstimatedDeliveryTime(seed.createdAt().plusHours(route.getSlaHours()));
        order.setExpectedDeliveryTime(seed.createdAt().plusHours(route.getSlaHours()));

        if (seed.status() == OrderStatus.DELIVERED) {
            order.setActualDeliveryTime(seed.createdAt().plusHours(10));
            order.setProofUrl("seed://proof/" + seed.code());
        } else if (seed.status() == OrderStatus.FAILED) {
            order.setActualDeliveryTime(seed.createdAt().plusHours(12));
        }
        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(Order order, ResolvedItem item, OrderStatus orderStatus) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(item.product());
        orderItem.setProductName(item.product().getProductName());
        orderItem.setQuantityOrdered(item.quantity());
        orderItem.setQuantityDelivered(orderStatus == OrderStatus.DELIVERED ? item.quantity() : null);
        orderItem.setUnitPrice(item.product().getPrice());
        orderItem.setWeightKg(item.product().getWeight());
        orderItem.setVolumeM3(item.volumeM3());
        orderItem.setTotalAmount(item.product().getPrice().multiply(BigDecimal.valueOf(item.quantity())));
        return orderItem;
    }

    private ResolvedItem resolveItem(ItemSeed item) {
        Product product = productRepository.findByProductCode(item.productCode())
                .orElseThrow(() -> new IllegalStateException("Product not found: " + item.productCode()));
        BigDecimal volume = product.getLength()
                .multiply(product.getWidth())
                .multiply(product.getHeight())
                .divide(new BigDecimal("1000000"), 6, RoundingMode.HALF_UP);
        return new ResolvedItem(product, item.quantity(), volume);
    }

    private void createTrackingHistory(Order order, OrderSeed seed) {
        createTracking(order, "Seed: order created", seed.latitude(), seed.longitude(), seed.createdAt());
        if (seed.status() != OrderStatus.NEW && seed.status() != OrderStatus.CANCELLED) {
            createTracking(
                    order,
                    "Seed: status " + seed.status().name(),
                    seed.latitude(),
                    seed.longitude(),
                    seed.createdAt().plusHours(2)
            );
        }
    }

    private void createTracking(
            Order order,
            String note,
            double latitude,
            double longitude,
            LocalDateTime recordedAt
    ) {
        if (orderTrackingRepository.existsByOrder_OrderIdAndNote(order.getOrderId(), note)) {
            return;
        }

        OrderTracking tracking = new OrderTracking();
        tracking.setOrder(order);
        tracking.setLatitude(BigDecimal.valueOf(latitude));
        tracking.setLongitude(BigDecimal.valueOf(longitude));
        tracking.setRecordedAt(recordedAt);
        tracking.setNote(note);
        orderTrackingRepository.save(tracking);
    }

    private void createOrderException(Order order) {
        ExceptionReason reason = exceptionReasonRepository
                .findByCategoryIgnoreCaseAndReasonTextIgnoreCase("CUSTOMER", "Customer was not available")
                .orElseThrow(() -> new IllegalStateException("Seed exception reason not found"));
        if (orderExceptionRepository
                .existsByOrder_OrderIdAndExceptionReason_ReasonId(order.getOrderId(), reason.getReasonId())) {
            return;
        }

        User reporter = userRepository.findByEmail("taixe4@logistics.com")
                .orElseThrow(() -> new IllegalStateException("Seed driver user not found"));
        OrderException exception = new OrderException();
        exception.setOrder(order);
        exception.setExceptionReason(reason);
        exception.setReportedBy(reporter);
        exception.setNotes("Customer could not be contacted after two delivery attempts");
        exception.setImageUrl("seed://exception/" + order.getOrderCode());
        orderExceptionRepository.save(exception);
    }

    private Point point(double longitude, double latitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(SRID_WGS_84);
        return point;
    }

    private record OrderSeed(
            String code,
            OrderStatus status,
            String customerEmail,
            String routeName,
            String zoneName,
            String deliveryAddress,
            String deliveryProvince,
            double longitude,
            double latitude,
            LocalDateTime createdAt,
            PaymentType paymentType,
            List<ItemSeed> items
    ) {
    }

    private record ItemSeed(String productCode, int quantity) {
    }

    private record ResolvedItem(Product product, int quantity, BigDecimal volumeM3) {
    }
}
