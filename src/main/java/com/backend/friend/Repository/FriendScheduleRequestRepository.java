package com.backend.friend.Repository;

import com.backend.friend.Entity.FriendScheduleRequest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendScheduleRequestRepository extends JpaRepository<FriendScheduleRequest, Long> {

    List<FriendScheduleRequest> findByReceiver_Id(Long recipientId);


}
