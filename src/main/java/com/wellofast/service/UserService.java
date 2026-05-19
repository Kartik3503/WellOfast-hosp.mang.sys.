package com.wellofast.service;

import com.wellofast.model.User;
import com.wellofast.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired private UserRepository repo;
    @Autowired private PasswordEncoder encoder;

    public User register(User u) {
        if (repo.existsByUsername(u.getUsername())) throw new RuntimeException("Username taken");
        u.setPassword(encoder.encode(u.getPassword()));
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        u.setActive(true);
        return repo.save(u);
    }

    public Optional<User> findByUsername(String username) { return repo.findByUsername(username); }
    public Optional<User> findById(String id) { return repo.findById(id); }
    public List<User> findAll() { return repo.findAll(); }
    public List<User> findByRole(String role) { return repo.findByRole(role); }
    public List<User> findActiveDoctors() { return repo.findByRoleAndActiveTrue("DOCTOR"); }
    public long countByRole(String role) { return repo.countByRole(role); }

    public User save(User u) { u.setUpdatedAt(LocalDateTime.now()); return repo.save(u); }

    public void delete(String id) { repo.deleteById(id); }
}
