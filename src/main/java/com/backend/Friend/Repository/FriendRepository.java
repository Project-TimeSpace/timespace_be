package com.backend.Friend.Repository;

import com.backend.Friend.Entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

}
