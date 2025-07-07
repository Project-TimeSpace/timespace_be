package com.backend.Friend.Repository;

import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import com.backend.Friend.Entity.FriendRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findAllByReceiverId(Long userId);

    boolean existsBySenderIdAndReceiverIdAndStatus(Long id, Long id1, RequestStatus requestStatus);
}
