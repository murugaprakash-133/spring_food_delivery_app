package com.examly.springapp.service;

import com.examly.springapp.entity.MenuItem;
import com.examly.springapp.entity.Restaurant;
import com.examly.springapp.repository.MenuRepository;
import com.examly.springapp.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuService(MenuRepository menuRepository, RestaurantRepository restaurantRepository) {
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public MenuItem addMenuItem(MenuItem menuItem) {
        Long restaurantId = menuItem.getRestaurant().getId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Error: Restaurant with ID " + restaurantId + " not found."));

        if (menuRepository.existsByNameAndRestaurantId(menuItem.getName(), restaurantId)) {
            throw new IllegalArgumentException(
                    "Error: Menu item '" + menuItem.getName() + "' already exists in this restaurant.");
        }

        menuItem.setRestaurant(restaurant);
        return menuRepository.save(menuItem);
    }

    public Page<MenuItem> getMenuItemsByRestaurant(
            Long restaurantId, 
            int page, 
            int size, 
            String sortBy, 
            String sortDir,
            String category,
            Boolean isVegetarian,
            Boolean isVegan,
            Boolean containsGluten,
            Boolean isAvailable) {
        
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("Error: Restaurant with ID " + restaurantId + " not found.");
        }

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (category != null || isVegetarian != null || isVegan != null || 
            containsGluten != null || isAvailable != null) {
            return menuRepository.findByRestaurantIdWithFilters(
                    restaurantId, category, isVegetarian, isVegan, 
                    containsGluten, isAvailable, pageRequest);
        }
        
        return menuRepository.findByRestaurantId(restaurantId, pageRequest);
    }

    public List<String> getMenuCategoriesByRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("Error: Restaurant with ID " + restaurantId + " not found.");
        }
        return menuRepository.findDistinctCategoriesByRestaurantId(restaurantId);
    }

    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuRepository.findById(id);
    }

    public Optional<MenuItem> updateMenuItem(Long id, MenuItem updatedMenuItem) {
        return menuRepository.findById(id).map(existingMenuItem -> {
            if (updatedMenuItem.getRestaurant() == null || updatedMenuItem.getRestaurant().getId() == null) {
                throw new IllegalArgumentException("Error: Restaurant information is required.");
            }

            Long restaurantId = updatedMenuItem.getRestaurant().getId();
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Error: Restaurant with ID " + restaurantId + " not found."));

            if (!existingMenuItem.getName().equals(updatedMenuItem.getName()) &&
                    menuRepository.existsByNameAndRestaurantId(updatedMenuItem.getName(), restaurantId)) {
                throw new IllegalArgumentException(
                        "Error: Menu item '" + updatedMenuItem.getName() + "' already exists in this restaurant.");
            }

            existingMenuItem.setName(updatedMenuItem.getName());
            existingMenuItem.setPrice(updatedMenuItem.getPrice());
            existingMenuItem.setDescription(updatedMenuItem.getDescription());
            existingMenuItem.setCategory(updatedMenuItem.getCategory());
            existingMenuItem.setAvailable(updatedMenuItem.getAvailable());
            existingMenuItem.setVegetarian(updatedMenuItem.getVegetarian());
            existingMenuItem.setVegan(updatedMenuItem.getVegan());
            existingMenuItem.setContainsGluten(updatedMenuItem.getContainsGluten());
            existingMenuItem.setImageUrl(updatedMenuItem.getImageUrl());
            existingMenuItem.setRestaurant(restaurant);

            return menuRepository.save(existingMenuItem);
        });
    }

    public String deleteMenuItem(Long id) {
        return menuRepository.findById(id)
                .map(menuItem -> {
                    menuRepository.delete(menuItem);
                    return "Menu item with ID " + id + " has been deleted successfully.";
                })
                .orElseThrow(() -> new IllegalArgumentException(
                        "Error: Menu item with ID " + id + " not found."));
    }
}