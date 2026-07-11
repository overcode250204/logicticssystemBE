package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.OrderCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.OrderItemRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.events.OrderCreatedEvent;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.OrderErrorCode;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.OrderMapper;
import com.overcode250204.smartlogicticssystem.repositories.*;
import com.overcode250204.smartlogicticssystem.services.IOrderService;
import com.overcode250204.smartlogicticssystem.services.S3FileService;
import com.overcode250204.smartlogicticssystem.utils.BarcodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RouteProvinceRepository routeProvinceRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final S3FileService s3FileService;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final ZoneRepository zoneRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateRequest request, int roleId, int userId) {
        if (roleId != 4) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }

        // Auto-Routing: Find route config by delivery province
        RouteProvince routeProvince = routeProvinceRepository.findByProvinceName(request.getDeliveryProvince());
        if (routeProvince == null) {
            throw new AppException(OrderErrorCode.DELIVERY_PROVINCE_UNSUPPORTED);
        }

        //Get zone to GROUP
        Zone zone = zoneRepository.findAreaContainingPoint(request.getLongitude(), request.getLatitude())
                .orElseThrow(() -> new AppException(OrderErrorCode.DELIVERY_PROVINCE_UNSUPPORTED));

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setPhone(request.getPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryProvince(request.getDeliveryProvince());
        order.setPaymentType(request.getPaymentType());
        order.setRouteConfig(routeProvince.getRouteConfig());
        order.setAssignedHub(routeProvince.getAssignedHub());
        order.setZone(zone);
        
        // Generate Order Code
        String orderCode = generateUniqueOrderCode();
        order.setOrderCode(orderCode);

        // Generate Barcode using the exact order code
        String barcodeData = orderCode; 
        BarcodeGeneratorUtil.GeneratedCode128Barcode generatedBarcode = BarcodeGeneratorUtil.generateCode128Barcode(barcodeData);
        String barcodeImageUrl = uploadBarcodeImage(generatedBarcode);
        order.setBarcodeUrl(barcodeImageUrl);

        // Map location
        Point deliveryPoint = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        // SRID 4326 is standard for GPS
        deliveryPoint.setSRID(4326);
        order.setDeliveryPoint(deliveryPoint);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolumeM3 = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = findByIdOrThrow(productRepository, itemRequest.getProductId(), OrderErrorCode.PRODUCT_NOT_FOUND);

            OrderItem item = getOrderItem(itemRequest, order, product);

            totalAmount = totalAmount.add(item.getTotalAmount());
            totalVolumeM3 = totalVolumeM3.add(item.getVolumeM3().multiply(BigDecimal.valueOf(item.getQuantityOrdered())));
            totalWeight = totalWeight.add(item.getWeightKg().multiply(BigDecimal.valueOf(item.getQuantityOrdered())));
            items.add(item);
        }

        order.setTotalAmount(totalAmount);
        order.setTotalWeightKg(totalWeight);
        order.setTotalVolumeM3(totalVolumeM3);
        
        Order savedOrder = orderRepository.save(order);
        items = orderItemRepository.saveAll(items);

        // Trigger Routing Engine seamlessly
        if (savedOrder.getRouteConfig() != null) {
            eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder.getRouteConfig().getRouteId()));
        }

        return orderMapper.toResponse(savedOrder, items);
    }

    private  OrderItem getOrderItem(OrderItemRequest itemRequest, Order order, Product product) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductName(product.getProductName());
        item.setQuantityOrdered(itemRequest.getQuantity());
        item.setUnitPrice(product.getPrice());
        item.setWeightKg(product.getWeight());
        item.setVolumeM3(calculateVolumeM3(product));
        item.setTotalAmount(calculateTotalAmount(itemRequest.getQuantity(), product.getPrice()));
        item.setProduct(product);
        return item;
    }

    // Calculate Total Amount for Item
    private BigDecimal calculateTotalAmount(Integer quantity, BigDecimal unitPrice){
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    // Calculate Volume for Item
    private BigDecimal calculateVolumeM3(Product product){
        BigDecimal length = product.getLength() != null ? product.getLength() : BigDecimal.ZERO;
        BigDecimal width = product.getWidth() != null ? product.getWidth() : BigDecimal.ZERO;
        BigDecimal height = product.getHeight() != null ? product.getHeight() : BigDecimal.ZERO;

        return  length.multiply(width).multiply(height).divide(new BigDecimal(1000000), RoundingMode.HALF_UP);
    }


    private String generateUniqueOrderCode() {
        String code;
        String chars = "0123456789";
        do {
            StringBuilder sb = new StringBuilder("ORD-");
            for (int i = 0; i < 12; i++) {
                sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
            }
            code = sb.toString();
        } while (orderRepository.existsByOrderCode(code));
        return code;
    }

    private String uploadBarcodeImage(BarcodeGeneratorUtil.GeneratedCode128Barcode generatedBarcode) {
        String key = "%s/%s.png".formatted("order-barcodes", generatedBarcode.barcode());
        return s3FileService.uploadBytes(generatedBarcode.pngBytes(), key, "image/png");
    }

    @Override
    public List<OrderResponseDTO> getAllOrders(int roleId, int userId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        List<Order> orders = orderRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        List<OrderItem> allItems = orderItemRepository.findByOrderIn(orders);
        java.util.Map<Long, List<OrderItem>> itemsByOrderId = allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getOrderId()));
        return orders.stream()
                .map(order -> orderMapper.toResponse(order, itemsByOrderId.getOrDefault(order.getOrderId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status, int roleId, int userId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        List<Order> orders = orderRepository.findByStatus(status, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        if (orders.isEmpty()) return new ArrayList<>();
        List<OrderItem> allItems = orderItemRepository.findByOrderIn(orders);
        java.util.Map<Long, List<OrderItem>> itemsByOrderId = allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getOrderId()));
        return orders.stream()
                .map(order -> orderMapper.toResponse(order, itemsByOrderId.getOrDefault(order.getOrderId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id, int roleId, int userId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        Order order = findByIdOrThrow(orderRepository, id, OrderErrorCode.ORDER_NOT_FOUND);
        List<OrderItem> items = orderItemRepository.findByOrderIn(List.of(order));
        return orderMapper.toResponse(order, items);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(Long id, int roleId, int userId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        Order order = findByIdOrThrow(orderRepository, id, OrderErrorCode.ORDER_NOT_FOUND);
        if (order.getStatus() != OrderStatus.NEW) {
            throw new AppException(OrderErrorCode.ORDER_CANNOT_BE_MODIFIED);
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderIn(List.of(savedOrder));
        return orderMapper.toResponse(savedOrder, items);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderCreateRequest request, int roleId, int userId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        Order order = findByIdOrThrow(orderRepository, id, OrderErrorCode.ORDER_NOT_FOUND);
        if (order.getStatus() != OrderStatus.NEW) {
            throw new AppException(OrderErrorCode.ORDER_CANNOT_BE_MODIFIED);
        }

        // Auto-Routing: Find route config by delivery province if it changed
        if (!order.getDeliveryProvince().equals(request.getDeliveryProvince())) {
            RouteProvince routeProvince = routeProvinceRepository.findByProvinceName(request.getDeliveryProvince());
            if (routeProvince == null) {
                throw new AppException(OrderErrorCode.DELIVERY_PROVINCE_UNSUPPORTED);
            }
            order.setRouteConfig(routeProvince.getRouteConfig());
            order.setAssignedHub(routeProvince.getAssignedHub());
        }

        // Get zone to GROUP if location coordinates changed
        boolean locationChanged = order.getDeliveryPoint() == null ||
                !request.getLatitude().equals(order.getDeliveryPoint().getY()) ||
                !request.getLongitude().equals(order.getDeliveryPoint().getX());
        if (locationChanged) {
            Zone zone = zoneRepository.findAreaContainingPoint(request.getLongitude(), request.getLatitude())
                    .orElseThrow(() -> new AppException(OrderErrorCode.DELIVERY_PROVINCE_UNSUPPORTED));
            order.setZone(zone);
            Point deliveryPoint = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            deliveryPoint.setSRID(4326);
            order.setDeliveryPoint(deliveryPoint);
        }

        order.setCustomerName(request.getCustomerName());
        order.setPhone(request.getPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryProvince(request.getDeliveryProvince());
        order.setPaymentType(request.getPaymentType());

        // Update items: First, remove old items
        List<OrderItem> oldItems = orderItemRepository.findByOrderIn(List.of(order));
        orderItemRepository.deleteAll(oldItems);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolumeM3 = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = findByIdOrThrow(productRepository, itemRequest.getProductId(), OrderErrorCode.PRODUCT_NOT_FOUND);
            OrderItem item = getOrderItem(itemRequest, order, product);

            totalAmount = totalAmount.add(item.getTotalAmount());
            totalVolumeM3 = totalVolumeM3.add(item.getVolumeM3().multiply(BigDecimal.valueOf(item.getQuantityOrdered())));
            totalWeight = totalWeight.add(item.getWeightKg().multiply(BigDecimal.valueOf(item.getQuantityOrdered())));
            items.add(item);
        }

        order.setTotalAmount(totalAmount);
        order.setTotalWeightKg(totalWeight);
        order.setTotalVolumeM3(totalVolumeM3);

        Order savedOrder = orderRepository.save(order);
        items = orderItemRepository.saveAll(items);

        // Trigger Routing Engine if route configuration exists
        if (savedOrder.getRouteConfig() != null) {
            eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder.getRouteConfig().getRouteId()));
        }

        return orderMapper.toResponse(savedOrder, items);
    }
}
