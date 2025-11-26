package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.Role;
import com.example.arkivebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<List<User>> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);

    double findByRoles(Set<Role> roles);

    /*@Query("SELECT u FROM User u LEFT JOIN FETCH u.userProfile WHERE u.username = :username")
    Optional<User> findByUsernameWithProfile(String username);*/
}
