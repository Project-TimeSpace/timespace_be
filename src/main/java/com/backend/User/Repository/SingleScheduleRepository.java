package com.backend.User.Repository;

import com.backend.User.Entity.SingleSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleScheduleRepository extends JpaRepository<SingleSchedule, Long> {
    List<SingleSchedule> findAllByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
    List<SingleSchedule> findAllByUserIdAndDate(Long userId, LocalDate date);

    List<SingleSchedule> findAllByUserIdAndDateIn(Long userId, List<LocalDate> occurrences);
}
