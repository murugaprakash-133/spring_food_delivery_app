package com.examly.springapp.entity;

import jakarta.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalAmount;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "special_instructions")
    private String specialInstructions;

    @Column(name = "delivery_fee", columnDefinition = "DECIMAL(10,2)")
    private Double deliveryFee;

    // Constructors
    public OrderEntity() {
        this.orderDate = LocalDateTime.now();
    }

    public OrderEntity(User user, Restaurant restaurant, String deliveryAddress) {
        this.user = user;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // Business logic methods
    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        if (orderItems != null) {
            double itemsTotal = orderItems.stream()
                    .mapToDouble(OrderItem::getTotalPrice)
                    .sum();
            this.totalAmount = itemsTotal + (deliveryFee != null ? deliveryFee : 0);
        }
    }

    @Transient
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @Transient
    public Long getRestaurantId() {
        return restaurant != null ? restaurant.getId() : null;
    }

    @Transient
    public List<String> getItemNames() {
        return orderItems.stream()
                .map(OrderItem::getItemName)
                .collect(Collectors.toList());
    }

    @Transient
    public Map<String, Integer> getItemQuantities() {
        return orderItems.stream()
                .collect(Collectors.toMap(
                        OrderItem::getItemName,
                        OrderItem::getQuantity,
                        (q1, q2) -> q1 + q2));
    }

    // Helper methods
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        calculateTotals();
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        calculateTotals();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        if (newStatus == OrderStatus.DELIVERED) {
            this.actualDeliveryTime = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
        calculateTotals();
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LocalDateTime getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + (orderItems != null ? orderItems.size() : 0) +
                '}';
    }
}