package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByName(String name);
    List<Permission> findByNameContaining(String filter);
    boolean existsByName(String name);
}
