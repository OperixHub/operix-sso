package com.operix.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.operix.auth.repository.SystemRepository;
import com.operix.auth.entity.System;

@Service
public class SystemService {

    private final SystemRepository systemRepository;

    public SystemService(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }

    public System save(System system) {
        system.setActive(true);
        return systemRepository.save(system);
    }

    public List<System> findAll() {
        return systemRepository.findAll();
    }

    public System findById(Long id) {
        return systemRepository.findById(id).orElseThrow(() -> new RuntimeException("Sistema não encontrado"));
    }

    public System findByUri(String uri) {
        return systemRepository.findByUri(uri).orElseThrow(() -> new RuntimeException("Sistema de origem não indentificado: " + uri));
    }

}
