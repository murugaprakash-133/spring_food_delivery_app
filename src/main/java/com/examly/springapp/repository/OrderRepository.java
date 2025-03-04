package com.examly.springapp.repository;

import com.examly.springapp.entity.OrderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId, Pageable pageable);
    List<OrderEntity> findByRestaurantId(Long restaurantId, Pageable pageable);
}
