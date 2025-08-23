package com.backend.User.Repository;

import com.backend.User.Entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long Id);

    Optional<User> findByEmail(String email);
}

