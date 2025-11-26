package com.example.arkivebackend.entity;

import com.example.arkivebackend.enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(name = "users")
@Entity
public class User extends BaseEntity{

    @Column(unique = true, nullable = false)
    String username;

    String fullName;

    @Email
    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    Set<Role> roles;

    @Column(nullable = false, columnDefinition = "boolean default false", name = "is_verified")
    boolean isVerified;

    @Enumerated(EnumType.STRING)
    AuthProvider authProvider;

    /*@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    UserProfile userProfile;*/
}
