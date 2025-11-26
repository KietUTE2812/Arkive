package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsByName(String name);
    java.util.Optional<Role> findByName(String name);
}
