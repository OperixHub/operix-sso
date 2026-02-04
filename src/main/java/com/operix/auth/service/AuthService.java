package com.operix.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.operix.auth.repository.SuperuserRepository;
import com.operix.auth.repository.UserRepository;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SuperuserRepository superuserRepository;

    public AuthService(UserRepository userRepository, SuperuserRepository superuserRepository) {
        this.userRepository = userRepository;
        this.superuserRepository = superuserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
            .or(() -> superuserRepository.findUserByEmail(username))
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
