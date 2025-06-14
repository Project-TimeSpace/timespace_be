package com.backend.UserSchedule.Repository;

import com.backend.UserSchedule.Entity.SingleSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleScheduleRepository extends JpaRepository<SingleSchedule, Long> {

}
