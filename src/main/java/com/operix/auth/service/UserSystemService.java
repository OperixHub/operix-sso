package com.operix.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.operix.auth.repository.UserSystemRepository;
import com.operix.auth.entity.UserSystem;

@Service
public class UserSystemService {
    
    private final UserSystemRepository userSystemRepository;

    public UserSystemService(UserSystemRepository userSystemRepository) {
        this.userSystemRepository = userSystemRepository;
    }

    public UserSystem save(UserSystem userSystem) {
        userSystem.setActive(true);
        return userSystemRepository.save(userSystem);
    }

    public List<UserSystem> findAll() {
        return userSystemRepository.findAll();
    }
}
