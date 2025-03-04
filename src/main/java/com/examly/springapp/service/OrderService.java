package com.examly.springapp.service;

import com.examly.springapp.entity.OrderEntity;
import com.examly.springapp.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderEntity createOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    public List<OrderEntity> getOrdersByUser(Long userId, int page, int size) {
        return orderRepository.findByUserId(userId, PageRequest.of(page, size));
    }

    public List<OrderEntity> getOrdersByRestaurant(Long restaurantId, int page, int size) {
        return orderRepository.findByRestaurantId(restaurantId, PageRequest.of(page, size));
    }

    public Optional<OrderEntity> updateOrder(Long orderId, OrderEntity updatedOrder) {
        return orderRepository.findById(orderId).map(existingOrder -> {
            existingOrder.setUser(updatedOrder.getUser()); // Set the entire User entity
            existingOrder.setRestaurant(updatedOrder.getRestaurant()); // Set the entire Restaurant entity
            existingOrder.setOrderItems(updatedOrder.getOrderItems()); // Set list of OrderItems
            existingOrder.setStatus(updatedOrder.getStatus()); // Set status if needed
            return orderRepository.save(existingOrder);
        });
    }

    public boolean deleteOrder(Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            orderRepository.delete(order);
            return true;
        }).orElse(false);
    }
}
