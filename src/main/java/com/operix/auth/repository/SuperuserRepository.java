package com.operix.auth.repository;

import com.operix.auth.entity.Superuser;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.security.core.userdetails.UserDetails;

@Repository
public interface SuperuserRepository extends JpaRepository<Superuser, Long> {
    Optional<UserDetails> findUserByEmail(String email);
}
