package com.backend.Group.Repository;

import com.backend.Group.Entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

}
