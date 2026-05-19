package com.wellofast.repository;

import com.wellofast.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByRoleAndActiveTrue(String role);
    List<User> findByDepartment(String department);
    long countByRole(String role);
    long countByRoleAndActiveTrue(String role);
}
