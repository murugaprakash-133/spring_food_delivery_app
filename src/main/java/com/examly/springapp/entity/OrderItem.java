package com.examly.springapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_order_time", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double priceAtOrderTime;

    @Column(name = "item_name_at_order_time", nullable = false)
    private String itemNameAtOrderTime;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double subtotal;

    @Transient
    public Double getTotalPrice() {
        return priceAtOrderTime * quantity;
    }

    @Transient
    public String getItemName() {
        return itemNameAtOrderTime;
    }

    @PrePersist
    @PreUpdate
    public void updateOrderDetails() {
        if (menuItem != null) {
            if (priceAtOrderTime == null) {
                priceAtOrderTime = menuItem.getPrice();
            }
            if (itemNameAtOrderTime == null) {
                itemNameAtOrderTime = menuItem.getName();
            }
            if (quantity != null && priceAtOrderTime != null) {
                this.subtotal = quantity * priceAtOrderTime;
            }
        }
    }
}