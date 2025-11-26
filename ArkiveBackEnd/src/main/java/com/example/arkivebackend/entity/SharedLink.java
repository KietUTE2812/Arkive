package com.example.arkivebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SharedLink extends  BaseEntity {

    @Column(nullable = false, unique = true)
    String publicId; // Unique public identifier for the shared link (like: /share/abc123)

    @Column(nullable = true)
    String password; // Hashed password for accessing the shared link (nullable if no password is set)

    /**
     * Mối quan hệ Một-Một (One-to-One) với Collection.
     * MỘT SharedLink chỉ thuộc về MỘT Collection.
     * 'optional = false' đảm bảo rằng một link không thể tồn tại
     * mà không gắn với collection nào.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collection_id", nullable = false)
    Collection collection;
}
