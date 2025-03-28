package com.examly.springapp.repository;

import com.examly.springapp.entity.OrderEntity;
import com.examly.springapp.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
    
    Page<OrderEntity> findByRestaurantId(Long restaurantId, Pageable pageable);
    
    @Query("SELECT o FROM OrderEntity o WHERE " +
           "o.restaurant.id = :restaurantId AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:fromDate IS NULL OR o.orderDate >= :fromDate) AND " +
           "(:toDate IS NULL OR o.orderDate <= :toDate)")
    Page<OrderEntity> findRestaurantOrdersWithFilters(
            @Param("restaurantId") Long restaurantId,
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
            
    @Query("SELECT o FROM OrderEntity o WHERE " +
           "o.user.id = :userId AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:fromDate IS NULL OR o.orderDate >= :fromDate) AND " +
           "(:toDate IS NULL OR o.orderDate <= :toDate)")
    Page<OrderEntity> findUserOrdersWithFilters(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
            
    @Query("SELECT SUM(oi.quantity * oi.priceAtOrderTime) FROM OrderItem oi " +
           "WHERE oi.order.restaurant.id = :restaurantId AND " +
           "oi.order.status = 'DELIVERED' AND " +
           "(:fromDate IS NULL OR oi.order.orderDate >= :fromDate) AND " +
           "(:toDate IS NULL OR oi.order.orderDate <= :toDate)")
    Double calculateRestaurantRevenue(
            @Param("restaurantId") Long restaurantId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    List<OrderEntity> findByUserIdOrderByOrderDateDesc(Long userId, PageRequest pageRequest);
    List<OrderEntity> findByRestaurantIdOrderByOrderDateDesc(Long restaurantId, PageRequest pageRequest);
    List<OrderEntity> findByStatus(OrderStatus status);
}