package com.examly.springapp.repository;

import com.examly.springapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY " +
           "CASE WHEN :sortBy = 'username' THEN u.username END, " +
           "CASE WHEN :sortBy = 'email' THEN u.email END, " +
           "CASE WHEN :sortBy = 'id' THEN u.id END")
    Page<User> findAllWithCustomSorting(@Param("sortBy") String sortBy, Pageable pageable);
}