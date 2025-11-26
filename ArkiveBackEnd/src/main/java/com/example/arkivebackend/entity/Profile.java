package com.example.arkivebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Profile extends BaseEntity {

    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @MapsId // Đảm bảo rằng UserProfile và User chia sẻ cùng một ID
    @JoinColumn(name = "user_id")
    User user;

    String phoneNumber;
    String address;
    String avatarUrl;

    @Column(columnDefinition = "TEXT")
    String bio;
}
