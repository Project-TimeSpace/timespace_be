package com.backend.Friend.Repository;

import com.backend.Friend.Entity.Friend;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUserId(Long userId);
    Optional<Friend> findByUserIdAndFriendId(Long userId, Long friendId);
}
