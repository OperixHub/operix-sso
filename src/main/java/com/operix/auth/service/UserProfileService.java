package com.operix.auth.service;

import org.springframework.stereotype.Service;
import com.operix.auth.repository.UserProfileRepository;
import com.operix.auth.entity.UserProfile;

import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile save(UserProfile userProfile) {
        userProfile.setActive(true);
        return userProfileRepository.save(userProfile);
    }

    public List<UserProfile> findAll() {
        return userProfileRepository.findAll();
    }

    public UserProfile findById(Long id) {
        return userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public UserProfile findByKeycloakId(String keycloakId) {
        return userProfileRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}
