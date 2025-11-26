package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedLinkRepository extends JpaRepository<SharedLink, String> {
    Optional<SharedLink> findByPublicId(String publicId);
    Optional<SharedLink> findByCollectionId(String collectionId);
    boolean existsByPublicId(String publicId);
}

