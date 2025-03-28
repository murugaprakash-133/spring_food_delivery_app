package com.examly.springapp.repository;

import com.examly.springapp.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId")
    Page<MenuItem> findByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE " +
           "m.restaurant.id = :restaurantId AND " +
           "(:category IS NULL OR m.category = :category) AND " +
           "(:isVegetarian IS NULL OR m.isVegetarian = :isVegetarian) AND " +
           "(:isVegan IS NULL OR m.isVegan = :isVegan) AND " +
           "(:containsGluten IS NULL OR m.containsGluten = :containsGluten) AND " +
           "(:isAvailable IS NULL OR m.isAvailable = :isAvailable)")
    Page<MenuItem> findByRestaurantIdWithFilters(
            @Param("restaurantId") Long restaurantId,
            @Param("category") String category,
            @Param("isVegetarian") Boolean isVegetarian,
            @Param("isVegan") Boolean isVegan,
            @Param("containsGluten") Boolean containsGluten,
            @Param("isAvailable") Boolean isAvailable,
            Pageable pageable);

    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.restaurant.id = :restaurantId")
    List<String> findDistinctCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);

    boolean existsByNameAndRestaurantId(String name, Long restaurantId);

    Optional<MenuItem> findByNameAndRestaurantId(String name, Long restaurantId);
}