package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;

import com.overcode250204.smartlogicticssystem.dtos.request.order.OrderItemRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.order.OrderRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.order.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.mapper.OrderMapper;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.ProductRepository;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.repositories.ZoneRepository;
import com.overcode250204.smartlogicticssystem.services.IOrderService;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@AllArgsConstructor
public class OrderService extends BaseServiceImpl implements IOrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final ZoneRepository zoneRepository;


    public OrderResponseDTO create(OrderRequest dto, int roleId, int userId) {
        //Kiểm tra customer
        User customer = userRepository.findById((long) userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Chuyển đổi tọa độ GPS từ Flutter gửi lên
        Point customerLocation = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));

        Zone zone = zoneRepository.findAreaContainingPoint(dto.getLongitude(), dto.getLatitude()).orElseThrow(() -> new RuntimeException("Not support this address!"));


        // Tạo nhanh đơn hàng ở trạng thái PENDING
        Order order = Order.builder()
                .customer(customer)
                .deliveryAddress(dto.getDeliveryAddress())
                .deliveryLocation(customerLocation)
                .status("PENDING")
                .zone(zone)
                .totalWeight(new BigDecimal(1000))
                .totalAmount(new BigDecimal("000.00"))
                .items(new ArrayList<>())
                .build();

        // Lưu chi tiết các sản phẩm trong đơn hàng
        for (OrderItemRequest itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
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
        return orderMapper.toResponse(order);
    }
    private BigDecimal calculateTotalAmount(BigDecimal price, Integer quantity){
        BigDecimal quantityBig = BigDecimal.valueOf(quantity);
        return price.multiply(quantityBig);
    }


    public OrderResponseDTO update(Long id, OrderRequest dto, int roleId, int userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderMapper.updateEntity(dto, order);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }


    public OrderResponseDTO getById(Long id, int roleId, int userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toResponse(order);
    }


    public void delete(Long id, int roleId, int userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(order);
    }
}
