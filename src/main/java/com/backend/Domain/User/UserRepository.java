package com.backend.Domain.User;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(Long Id);

    Optional<User> findByEmail(String email);
}

