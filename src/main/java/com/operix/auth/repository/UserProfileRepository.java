package com.operix.auth.repository;

import com.operix.auth.entity.UserProfile;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findUserByEmail(String email);
    Optional<UserProfile> findByKeycloakId(String keycloakId);
}
