package com.operix.auth.service;

import org.springframework.stereotype.Service;
import com.operix.auth.repository.SuperuserRepository;
import com.operix.auth.entity.Superuser;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class SuperuserService {

    private final SuperuserRepository superuserRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperuserService(SuperuserRepository superuserRepository, PasswordEncoder passwordEncoder) {
        this.superuserRepository = superuserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Superuser save(Superuser superuser) {
        superuser.setPassword(passwordEncoder.encode(superuser.getPassword()));
        superuser.setActive(true);
        return superuserRepository.save(superuser);
    }

    public List<Superuser> findAll() {
        return superuserRepository.findAll();
    }

    public Superuser findById(Long id) {
        return superuserRepository.findById(id).orElseThrow(() -> new RuntimeException("Super usuário não encontrado"));
    }

}
