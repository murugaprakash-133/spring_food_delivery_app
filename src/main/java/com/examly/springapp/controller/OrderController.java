package com.examly.springapp.controller;

import com.examly.springapp.entity.*;
import com.examly.springapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Operations pertaining to orders in the system")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User or restaurant not found")
    })
    public ResponseEntity<?> createOrder(
            @Parameter(description = "Order object that needs to be created", required = true)
            @RequestBody OrderEntity order) {
        try {
            OrderEntity createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(201).body(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating order");
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by user ID", description = "Retrieves paginated and filtered orders for a specific user")
    public ResponseEntity<Page<OrderEntity>> getOrdersByUser(
            @Parameter(description = "ID of the user", example = "1", required = true)
            @PathVariable Long userId,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field", schema = @Schema(allowableValues = {"orderDate", "totalAmount", "status"}))
            @RequestParam(defaultValue = "orderDate") String sortBy,
            
            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String sortDir,
            
            @Parameter(description = "Filter by status", schema = @Schema(implementation = OrderStatus.class))
            @RequestParam(required = false) OrderStatus status,
            
            @Parameter(description = "Filter from date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            
            @Parameter(description = "Filter to date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        
        Page<OrderEntity> orders = orderService.getOrdersByUser(userId, page, size, sortBy, sortDir, status, fromDate, toDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get orders by restaurant ID", description = "Retrieves paginated and filtered orders for a specific restaurant")
    public ResponseEntity<Page<OrderEntity>> getOrdersByRestaurant(
            @Parameter(description = "ID of the restaurant", example = "1", required = true)
            @PathVariable Long restaurantId,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field", schema = @Schema(allowableValues = {"orderDate", "totalAmount", "status"}))
            @RequestParam(defaultValue = "orderDate") String sortBy,
            
            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String sortDir,
            
            @Parameter(description = "Filter by status", schema = @Schema(implementation = OrderStatus.class))
            @RequestParam(required = false) OrderStatus status,
            
            @Parameter(description = "Filter from date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            
            @Parameter(description = "Filter to date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        
        Page<OrderEntity> orders = orderService.getOrdersByRestaurant(restaurantId, page, size, sortBy, sortDir, status, fromDate, toDate);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<?> updateOrderStatus(
            @Parameter(description = "ID of the order to update", example = "1", required = true)
            @PathVariable Long orderId,
            
            @Parameter(description = "New status for the order", required = true)
            @RequestParam OrderStatus status) {
        
        try {
            return orderService.updateOrderStatus(orderId, status)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel an order", description = "Cancels an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot cancel order in current status"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<?> cancelOrder(
            @Parameter(description = "ID of the order to cancel", example = "1", required = true)
            @PathVariable Long orderId) {
        
        try {
            return orderService.cancelOrder(orderId)
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/restaurant/{restaurantId}/revenue")
    @Operation(summary = "Calculate restaurant revenue", description = "Calculates total revenue for a restaurant with optional date filters")
    public ResponseEntity<Double> calculateRestaurantRevenue(
            @Parameter(description = "ID of the restaurant", example = "1", required = true)
            @PathVariable Long restaurantId,
            
            @Parameter(description = "Filter from date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            
            @Parameter(description = "Filter to date (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        
        Double revenue = orderService.calculateRestaurantRevenue(restaurantId, fromDate, toDate);
        return ResponseEntity.ok(revenue != null ? revenue : 0.0);
    }

    @GetMapping("/{orderId}/details")
    @Operation(summary = "Get detailed order information")
    public ResponseEntity<OrderEntity> getOrderDetails(
            @PathVariable Long orderId) {
        OrderEntity order = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(order);
    }
}