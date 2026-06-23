package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.OrderCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.OrderItemRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.entities.OrderItem;
import com.overcode250204.smartlogicticssystem.entities.Product;
import com.overcode250204.smartlogicticssystem.entities.RouteProvince;
import com.overcode250204.smartlogicticssystem.events.OrderCreatedEvent;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.OrderErrorCode;
import com.overcode250204.smartlogicticssystem.exception.RoleErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.OrderMapper;
import com.overcode250204.smartlogicticssystem.repositories.OrderItemRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteProvinceRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setPhone(request.getPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryProvince(request.getDeliveryProvince());
        order.setPaymentType(request.getPaymentType());
        order.setRouteConfig(routeProvince.getRouteConfig());
        order.setAssignedHub(routeProvince.getAssignedHub());
        
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
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = findByIdOrThrow(productRepository, itemRequest.getProductId(), OrderErrorCode.PRODUCT_NOT_FOUND);
            
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductName(product.getProductName());
            item.setQuantityOrdered(itemRequest.getQuantity());
            item.setUnitPrice(product.getPrice());
            
            // Weight calculation
            BigDecimal weight = product.getWeight() != null ? product.getWeight() : BigDecimal.ZERO;
            item.setWeightKg(weight.multiply(new BigDecimal(itemRequest.getQuantity())));
            
            // Hardcoded volume as per user specification until Product is modified
            BigDecimal defaultVolume = new BigDecimal("0.01");
            item.setVolumeM3(defaultVolume.multiply(new BigDecimal(itemRequest.getQuantity())));
            
            totalAmount = totalAmount.add(item.getUnitPrice().multiply(new BigDecimal(itemRequest.getQuantity())));
            items.add(item);
        }

        order.setTotalAmount(totalAmount);
        
        Order savedOrder = orderRepository.save(order);
        items = orderItemRepository.saveAll(items);

        // Trigger Routing Engine seamlessly!
        if (savedOrder.getRouteConfig() != null) {
            eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder.getRouteConfig().getRouteId()));
        }

        return orderMapper.toResponse(savedOrder, items);
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
}
