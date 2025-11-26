package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    
}
