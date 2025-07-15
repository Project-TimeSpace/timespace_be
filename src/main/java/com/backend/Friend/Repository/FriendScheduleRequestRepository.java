package com.backend.Friend.Repository;

import com.backend.Friend.Entity.FriendScheduleRequest;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendScheduleRequestRepository extends JpaRepository<FriendScheduleRequest, Long> {

    List<FriendScheduleRequest> findByReceiver_Id(Long recipientId);


}
