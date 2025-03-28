package com.examly.springapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.*;
import java.util.Objects;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double price;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Column(nullable = false)
    private Boolean isVegetarian = false;

    @Column(nullable = false)
    private Boolean isVegan = false;

    @Column(nullable = false)
    private Boolean containsGluten = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Restaurant restaurant;

    @Column
    private String imageUrl;

    // Constructors, methods, and other fields remain the same...
    public MenuItem() {
    }

    public MenuItem(String name, Double price, String category, Restaurant restaurant) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.restaurant = restaurant;
    }

    // Business logic methods
    public boolean matchesDietaryRequirements(boolean vegetarian, boolean vegan, boolean glutenFree) {
        return (!vegetarian || isVegetarian) &&
               (!vegan || isVegan) &&
               (!glutenFree || !containsGluten);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Boolean getVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(Boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public Boolean getVegan() {
        return isVegan;
    }

    public void setVegan(Boolean vegan) {
        isVegan = vegan;
    }

    public Boolean getContainsGluten() {
        return containsGluten;
    }

    public void setContainsGluten(Boolean containsGluten) {
        this.containsGluten = containsGluten;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(id, menuItem.id) &&
               Objects.equals(name, menuItem.name) &&
               Objects.equals(restaurant, menuItem.restaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, restaurant);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", restaurant=" + (restaurant != null ? restaurant.getName() : "null") +
                '}';
    }
}