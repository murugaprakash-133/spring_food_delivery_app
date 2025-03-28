package com.examly.springapp.controller;

import com.examly.springapp.entity.User;
import com.examly.springapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations pertaining to users in the system")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public ResponseEntity<?> createUser(
            @Parameter(description = "User object that needs to be created", required = true)
            @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(201).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: Unable to create user.");
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Returns a single user by their email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUserByEmail(
            @Parameter(description = "Email address of the user to be retrieved", example = "user@example.com", required = true)
            @PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Returns a single user by their username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUserByUsername(
            @Parameter(description = "Username of the user to be retrieved", example = "john_doe", required = true)
            @PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns a paginated and sorted list of users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "204", description = "No users found")
    })
    public ResponseEntity<Page<User>> getAllUsers(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field (id, username, email)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(description = "Sort direction (asc, desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortOrder) {
        
        Page<User> users = userService.getAllUsers(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users with pagination and sorting")
    public ResponseEntity<Page<User>> searchUsers(
            @Parameter(description = "Search term for username or email")
            @RequestParam String query,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort by field (id, username, email)", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            
            @Parameter(description = "Sort direction (asc, desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortOrder) {
        
        Page<User> users = userService.searchUsers(query, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a single user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user to be retrieved", example = "1", required = true)
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to be updated", example = "1", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "Updated user object", required = true)
            @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted", example = "1", required = true)
            @PathVariable Long id) {
        return userService.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}