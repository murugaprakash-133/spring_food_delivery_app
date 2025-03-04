package com.examly.springapp.service;

import com.examly.springapp.entity.Restaurant;
import com.examly.springapp.repository.RestaurantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant registerRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Optional<Restaurant> updateRestaurant(Long id, Restaurant updatedRestaurant) {
        return restaurantRepository.findById(id).map(existingRestaurant -> {
            existingRestaurant.setName(updatedRestaurant.getName());
            existingRestaurant.setAddress(updatedRestaurant.getAddress());
            existingRestaurant.setCuisine(updatedRestaurant.getCuisine());
            return restaurantRepository.save(existingRestaurant);
        });
    }

    public boolean deleteRestaurant(Long id) {
        return restaurantRepository.findById(id).map(restaurant -> {
            restaurantRepository.delete(restaurant);
            return true;
        }).orElse(false);
    }

    public List<Restaurant> getPaginatedRestaurants(int page, int size) {
        return restaurantRepository.findAll(PageRequest.of(page, size)).getContent();
    }
}
