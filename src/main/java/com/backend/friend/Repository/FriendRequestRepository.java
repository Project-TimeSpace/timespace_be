package com.backend.friend.Repository;

import com.backend.friend.Entity.FriendRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findAllByReceiverId(Long userId);

    boolean existsBySenderIdAndReceiverId(Long id, Long id1);
}
