package com.backend.Group.Repository;

import com.backend.Group.Entity.GroupMembers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {

}
