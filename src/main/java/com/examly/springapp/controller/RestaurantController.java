package com.examly.springapp.controller;

import com.examly.springapp.entity.Restaurant;
import com.examly.springapp.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurant Management", description = "Operations pertaining to restaurants in the system")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @Operation(summary = "Register a new restaurant", description = "Registers a new restaurant in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restaurant registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public ResponseEntity<Restaurant> registerRestaurant(
            @Parameter(description = "Restaurant object that needs to be registered", required = true)
            @RequestBody Restaurant restaurant) {
        Restaurant createdRestaurant = restaurantService.registerRestaurant(restaurant);
        return ResponseEntity.status(201).body(createdRestaurant);
    }

    @GetMapping
    @Operation(summary = "Get all restaurants", description = "Retrieves a paginated and sorted list of all restaurants")
    public ResponseEntity<Page<Restaurant>> getAllRestaurants(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field", schema = @Schema(
                allowableValues = {"id", "name", "cuisine", "phoneNumber", "openingHours"}))
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction", schema = @Schema(
                allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        return ResponseEntity.ok(restaurantService.getAllRestaurants(page, size, sortBy, sortDir));
    }

    @GetMapping("/search")
    @Operation(summary = "Search restaurants", description = "Searches restaurants with pagination and sorting")
    public ResponseEntity<Page<Restaurant>> searchRestaurants(
            @Parameter(description = "Search term for name or cuisine")
            @RequestParam String query,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field", schema = @Schema(
                allowableValues = {"id", "name", "cuisine", "phoneNumber", "openingHours"}))
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction", schema = @Schema(
                allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        return ResponseEntity.ok(restaurantService.searchRestaurants(query, page, size, sortBy, sortDir));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter by cuisine", description = "Filters restaurants by cuisine with pagination and sorting")
    public ResponseEntity<Page<Restaurant>> filterByCuisine(
            @Parameter(description = "Cuisine type to filter by")
            @RequestParam String cuisine,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field", schema = @Schema(
                allowableValues = {"id", "name", "cuisine", "phoneNumber", "openingHours"}))
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction", schema = @Schema(
                allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        return ResponseEntity.ok(restaurantService.filterByCuisine(cuisine, page, size, sortBy, sortDir));
    }

    @GetMapping("/cuisines")
    @Operation(summary = "Get all cuisine types", description = "Retrieves a list of all available cuisine types")
    public ResponseEntity<List<String>> getAllCuisineTypes() {
        return ResponseEntity.ok(restaurantService.getAllCuisineTypes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieves a specific restaurant by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant found"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<Restaurant> getRestaurantById(
            @Parameter(description = "ID of the restaurant to be retrieved", example = "1", required = true)
            @PathVariable Long id) {
        return restaurantService.getRestaurantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant", description = "Updates an existing restaurant with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<Restaurant> updateRestaurant(
            @Parameter(description = "ID of the restaurant to be updated", example = "1", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "Updated restaurant object", required = true)
            @RequestBody Restaurant restaurant) {
        return restaurantService.updateRestaurant(id, restaurant)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant", description = "Deletes a restaurant by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<Void> deleteRestaurant(
            @Parameter(description = "ID of the restaurant to be deleted", example = "1", required = true)
            @PathVariable Long id) {
        return restaurantService.deleteRestaurant(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}