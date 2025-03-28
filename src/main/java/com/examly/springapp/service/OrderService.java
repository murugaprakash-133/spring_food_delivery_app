package com.examly.springapp.service;

import com.examly.springapp.entity.*;
import com.examly.springapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                       UserRepository userRepository,
                       RestaurantRepository restaurantRepository,
                       MenuItemRepository menuItemRepository,
                       OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderEntity createOrder(OrderEntity order) {
        // Validate user and restaurant
        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        // Set user and restaurant
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Save order first to get the ID
        OrderEntity savedOrder = orderRepository.save(order);

        // Process order items
        for (OrderItem item : order.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(item.getMenuItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("MenuItem not found: " + item.getMenuItem().getId()));

            item.setOrder(savedOrder);
            item.setMenuItem(menuItem);
            item.setPriceAtOrderTime(menuItem.getPrice());
            item.setItemNameAtOrderTime(menuItem.getName());
            item.setSubtotal(item.getTotalPrice());

            orderItemRepository.save(item);
        }

        // Calculate total amount
        order.calculateTotals();
        return orderRepository.save(order);
    }

    public Page<OrderEntity> getOrdersByUser(Long userId, int page, int size, String sortBy, String sortOrder,
                                           OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return orderRepository.findUserOrdersWithFilters(userId, status, fromDate, toDate, pageRequest);
    }

    public Page<OrderEntity> getOrdersByRestaurant(Long restaurantId, int page, int size, String sortBy, String sortOrder,
                                                 OrderStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return orderRepository.findRestaurantOrdersWithFilters(restaurantId, status, fromDate, toDate, pageRequest);
    }

    @Transactional
    public Optional<OrderEntity> updateOrder(Long orderId, OrderEntity orderDetails) {
        return orderRepository.findById(orderId).map(order -> {
            // Update basic order details
            order.setDeliveryAddress(orderDetails.getDeliveryAddress());
            order.setSpecialInstructions(orderDetails.getSpecialInstructions());
            order.setDeliveryFee(orderDetails.getDeliveryFee());

            // Update order items
            order.getOrderItems().clear();
            for (OrderItem item : orderDetails.getOrderItems()) {
                MenuItem menuItem = menuItemRepository.findById(item.getMenuItem().getId())
                        .orElseThrow(() -> new IllegalArgumentException("MenuItem not found: " + item.getMenuItem().getId()));

                item.setOrder(order);
                item.setMenuItem(menuItem);
                item.setPriceAtOrderTime(menuItem.getPrice());
                item.setItemNameAtOrderTime(menuItem.getName());
                item.setSubtotal(item.getTotalPrice());

                orderItemRepository.save(item);
                order.addOrderItem(item);
            }

            // Recalculate totals
            order.calculateTotals();
            return orderRepository.save(order);
        });
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        orderRepository.delete(order);
    }

    @Transactional
    public Optional<OrderEntity> updateOrderStatus(Long orderId, OrderStatus newStatus) {
        return orderRepository.findById(orderId).map(order -> {
            if (order.getStatus() == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
                throw new IllegalStateException("Cannot update status of a cancelled order");
            }
            if (order.getStatus() == OrderStatus.DELIVERED && newStatus != OrderStatus.DELIVERED) {
                throw new IllegalStateException("Cannot update status of a delivered order");
            }
            order.updateStatus(newStatus);
            return orderRepository.save(order);
        });
    }

    public Optional<OrderEntity> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<OrderEntity> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        try {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
            if (order.getStatus() == OrderStatus.DELIVERED) {
                throw new IllegalStateException("Cannot cancel a delivered order");
            }
            order.updateStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Double calculateRestaurantRevenue(Long restaurantId, LocalDateTime fromDate, LocalDateTime toDate) {
        return orderRepository.calculateRestaurantRevenue(restaurantId, fromDate, toDate);
    }

    public OrderEntity getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }
}