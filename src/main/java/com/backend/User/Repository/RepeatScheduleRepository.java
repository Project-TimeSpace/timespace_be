package com.backend.User.Repository;

import com.backend.User.Entity.RepeatSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatScheduleRepository extends JpaRepository<RepeatSchedule, Long> {
    List<RepeatSchedule> findAllByUserId(Long userId);

    // Less와 Greater가 이게 맞는지.. 고민됨
    List<RepeatSchedule> findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long userId, LocalDate end, LocalDate start);

}
