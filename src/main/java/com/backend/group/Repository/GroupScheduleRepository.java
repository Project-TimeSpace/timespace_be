package com.backend.group.Repository;

import com.backend.group.Entity.GroupSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, Long> {

    List<GroupSchedule> findByGroupId(Long groupId);

    List<GroupSchedule> findByGroupIdInAndDateBetween(List<Long> groupIds, LocalDate start, LocalDate end);
}