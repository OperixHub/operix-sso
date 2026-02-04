package com.operix.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.operix.auth.entity.UserSystem;

public interface UserSystemRepository extends JpaRepository<UserSystem, Long> {
    
}
