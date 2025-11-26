package com.example.arkivebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Asset extends  BaseEntity {
    @Column(nullable = false)
    String filename;

    @Column(nullable = false, unique = true)
    String storageKey;

    @Column(nullable = false)
    String fileType; // MIME type like "image/png", "video/mp4"

    @Column(nullable = false)
    Long fileSize; // in bytes

    @Column(nullable = true)
    String thumbnailUrl;

    @Column(nullable = true)
    Set<String> tags;

    // --- Relationships with Collection
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    Collection collection;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean isDeleted = false;
}
