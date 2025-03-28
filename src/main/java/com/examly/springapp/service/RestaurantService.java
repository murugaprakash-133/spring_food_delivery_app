package com.examly.springapp.service;

import com.examly.springapp.entity.Restaurant;
import com.examly.springapp.repository.RestaurantRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant registerRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Page<Restaurant> getAllRestaurants(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return restaurantRepository.findAll(pageable);
    }

    public Page<Restaurant> searchRestaurants(String searchTerm, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return restaurantRepository.searchWithSorting(searchTerm, sortBy, pageable);
    }

    public Page<Restaurant> filterByCuisine(String cuisine, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return restaurantRepository.filterByCuisineWithSorting(cuisine, sortBy, pageable);
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Optional<Restaurant> updateRestaurant(Long id, Restaurant updatedRestaurant) {
        return restaurantRepository.findById(id).map(existingRestaurant -> {
            existingRestaurant.setName(updatedRestaurant.getName());
            existingRestaurant.setAddress(updatedRestaurant.getAddress());
            existingRestaurant.setCuisine(updatedRestaurant.getCuisine());
            existingRestaurant.setPhoneNumber(updatedRestaurant.getPhoneNumber());
            existingRestaurant.setOpeningHours(updatedRestaurant.getOpeningHours());
            return restaurantRepository.save(existingRestaurant);
        });
    }

    public boolean deleteRestaurant(Long id) {
        return restaurantRepository.findById(id).map(restaurant -> {
            restaurantRepository.delete(restaurant);
            return true;
        }).orElse(false);
    }

    public List<String> getAllCuisineTypes() {
        return restaurantRepository.findAllCuisineTypes();
    }
}