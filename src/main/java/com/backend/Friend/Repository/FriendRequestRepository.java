package com.backend.Friend.Repository;

import com.backend.Friend.Entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

}
