package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, String> {
    Optional<Collection> findByName(String name);
    Optional<Collection> findByNameAndOwnerId(String name, String ownerId);
    List<Collection> findAllByOwnerId(String ownerId);
}
