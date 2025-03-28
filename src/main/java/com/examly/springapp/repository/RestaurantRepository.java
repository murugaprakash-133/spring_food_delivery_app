package com.examly.springapp.repository;

import com.examly.springapp.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT r FROM Restaurant r ORDER BY " +
           "CASE WHEN :sortBy = 'name' THEN r.name END, " +
           "CASE WHEN :sortBy = 'cuisine' THEN r.cuisine END, " +
           "CASE WHEN :sortBy = 'id' THEN r.id END")
    Page<Restaurant> findAllWithCustomSorting(@Param("sortBy") String sortBy, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'name' THEN r.name END, " +
           "CASE WHEN :sortBy = 'cuisine' THEN r.cuisine END, " +
           "CASE WHEN :sortBy = 'id' THEN r.id END")
    Page<Restaurant> searchWithSorting(
            @Param("searchTerm") String searchTerm,
            @Param("sortBy") String sortBy,
            Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE " +
           "(:cuisine IS NULL OR r.cuisine = :cuisine) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'name' THEN r.name END, " +
           "CASE WHEN :sortBy = 'cuisine' THEN r.cuisine END, " +
           "CASE WHEN :sortBy = 'id' THEN r.id END")
    Page<Restaurant> filterByCuisineWithSorting(
            @Param("cuisine") String cuisine,
            @Param("sortBy") String sortBy,
            Pageable pageable);

    @Query("SELECT DISTINCT r.cuisine FROM Restaurant r")
    List<String> findAllCuisineTypes();
}