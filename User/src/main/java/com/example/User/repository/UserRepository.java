package com.example.User.repository;

import com.example.User.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return an Optional containing the User if found, or empty if not found
     */

    Optional<User> findByEmail(String email);
}
