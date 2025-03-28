package com.examly.springapp.controller;

import com.examly.springapp.entity.MenuItem;
import com.examly.springapp.service.MenuService;
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
@RequestMapping("/menu")
@Tag(name = "Menu Management", description = "Operations pertaining to menu items in the system")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    @Operation(summary = "Add a new menu item", description = "Creates a new menu item in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Menu item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<MenuItem> addMenuItem(
            @Parameter(description = "Menu item object that needs to be added", required = true)
            @RequestBody MenuItem menuItem) {
        return ResponseEntity.status(201).body(menuService.addMenuItem(menuItem));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get menu items by restaurant", 
               description = "Retrieves paginated and filtered menu items for a specific restaurant")
    public ResponseEntity<Page<MenuItem>> getMenuItemsByRestaurant(
            @Parameter(description = "ID of the restaurant", example = "1", required = true)
            @PathVariable Long restaurantId,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field", schema = @Schema(
                allowableValues = {"id", "name", "price", "category"}))
            @RequestParam(defaultValue = "name") String sortBy,
            
            @Parameter(description = "Sort direction", schema = @Schema(
                allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "asc") String sortDir,
            
            @Parameter(description = "Filter by category")
            @RequestParam(required = false) String category,
            
            @Parameter(description = "Filter by vegetarian status")
            @RequestParam(required = false) Boolean vegetarian,
            
            @Parameter(description = "Filter by vegan status")
            @RequestParam(required = false) Boolean vegan,
            
            @Parameter(description = "Filter by gluten-free status")
            @RequestParam(required = false) Boolean glutenFree,
            
            @Parameter(description = "Filter by availability")
            @RequestParam(required = false) Boolean available) {
        
        return ResponseEntity.ok(menuService.getMenuItemsByRestaurant(
                restaurantId, page, size, sortBy, sortDir, 
                category, vegetarian, vegan, glutenFree, available));
    }

    @GetMapping("/restaurant/{restaurantId}/categories")
    @Operation(summary = "Get menu categories by restaurant", 
               description = "Retrieves all distinct menu categories for a restaurant")
    public ResponseEntity<List<String>> getMenuCategoriesByRestaurant(
            @Parameter(description = "ID of the restaurant", example = "1", required = true)
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenuCategoriesByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get menu item by ID", description = "Retrieves a specific menu item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu item found"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    public ResponseEntity<MenuItem> getMenuItemById(
            @Parameter(description = "ID of the menu item to be retrieved", example = "1", required = true)
            @PathVariable Long id) {
        return menuService.getMenuItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu item", description = "Updates an existing menu item with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    public ResponseEntity<MenuItem> updateMenuItem(
            @Parameter(description = "ID of the menu item to be updated", example = "1", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "Updated menu item object", required = true)
            @RequestBody MenuItem menuItem) {
        return menuService.updateMenuItem(id, menuItem)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu item", description = "Removes a menu item from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu item deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    public ResponseEntity<String> deleteMenuItem(
            @Parameter(description = "ID of the menu item to be deleted", example = "1", required = true)
            @PathVariable Long id) {
        try {
            String message = menuService.deleteMenuItem(id);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}