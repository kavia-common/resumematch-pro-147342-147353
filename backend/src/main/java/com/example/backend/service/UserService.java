package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * User management and UserDetailsService implementation.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository users, BCryptPasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    // PUBLIC_INTERFACE
    public User register(String email, String rawPassword) {
        String hash = encoder.encode(rawPassword);
        User u = new User(email.toLowerCase(), hash);
        return users.save(u);
    }

    public Optional<User> findByEmail(String email) {
        return users.findByEmail(email.toLowerCase());
    }

    // PUBLIC_INTERFACE
    public boolean verifyPassword(String raw, String hash) {
        return encoder.matches(raw, hash);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = users.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
