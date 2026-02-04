package com.operix.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.operix.auth.entity.System;

public interface SystemRepository extends JpaRepository<System, Long> {
    Optional<System> findByUri(String uri);
}
