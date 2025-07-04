package com.backend.User.Repository;

import com.backend.User.Entity.UserCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Integer> {
    List<UserCategory> findByUserId(Long userId);

    Optional<UserCategory> findByUserIdAndCategoryName(Long userId, String categoryName);

    boolean existsByUserIdAndCategoryName(Long userId, String categoryName);

    Optional<UserCategory> findByUserIdAndCategoryId(Long userId, Integer categoryId);

    List<UserCategory> findByUserIdOrderByCategoryId(Long userId);
}
