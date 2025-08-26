package com.backend.user.Repository;

import com.backend.user.Entity.RepeatSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatScheduleRepository extends JpaRepository<RepeatSchedule, Long> {
    List<RepeatSchedule> findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long userId, LocalDate end, LocalDate start);
}
