package com.backend.friend.Repository;

import com.backend.friend.Entity.Friend;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUserId(Long userId);
    Optional<Friend> findByUser_IdAndFriend_Id(Long userId, Long friendId);

    boolean existsByUserIdAndFriendId(Long inviterId, Long friendId);

}
