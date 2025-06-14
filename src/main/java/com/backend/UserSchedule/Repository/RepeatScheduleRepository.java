package com.backend.UserSchedule.Repository;

import com.backend.UserSchedule.Entity.RepeatSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatScheduleRepository extends JpaRepository<RepeatSchedule, Long> {

}
