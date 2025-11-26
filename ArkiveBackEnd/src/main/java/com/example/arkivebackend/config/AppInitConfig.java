package com.example.arkivebackend.config;

import com.example.arkivebackend.entity.Permission;
import com.example.arkivebackend.entity.Role;
import com.example.arkivebackend.entity.User;
import com.example.arkivebackend.repository.PermissionRepository;
import com.example.arkivebackend.repository.RoleRepository;
import com.example.arkivebackend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Value("${app.default-admin-password}")
    @NonFinal
    String defaultAdminPassword;

    @Value("${app.default-admin-username}")
    @NonFinal
    String defaultAdminUsername;

    @Value("${app.default-admin-email}")
    @NonFinal
    String defaultAdminEmail;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> initAdmin(userRepository);
    }

    @Transactional
    void initAdmin(UserRepository userRepository) {
        if (userRepository.findByEmail(defaultAdminEmail).isPresent()) {
            return; // đã có user thì bỏ qua
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

        Set<Permission> adminPermissions = Set.of(
                Permission.builder().name("USER_MANAGE").description("Use user management features").build(),
                Permission.builder().name("ASSET_MANAGE").description("Use asset management features").build(),
                Permission.builder().name("PAYMENT_MANAGE").description("Use payment management features").build()
                // Thêm các quyền khác nếu cần
        );
        permissionRepository.saveAll(adminPermissions);

        adminRole.setPermissions(new HashSet<>(adminPermissions));
        roleRepository.save(adminRole);

        User adminUser = User.builder()
                .username(defaultAdminUsername)
                .email(defaultAdminEmail)
                .password(passwordEncoder.encode(defaultAdminPassword))
                .roles(Set.of(adminRole))
                .isVerified(true)
                .fullName("Akrive Admin")
                .build();

        userRepository.save(adminUser);
    }
}
