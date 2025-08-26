package com.backend.user.Repository;

import com.backend.user.Entity.RepeatException;
import com.backend.user.Entity.RepeatSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatExceptionRepository extends JpaRepository<RepeatException, Long> {
    List<RepeatException> findAllByRepeatScheduleIdInAndExceptionDateBetween(List<Long> repeatScheduleIds, LocalDate start, LocalDate end);
    boolean existsByRepeatScheduleAndExceptionDate(RepeatSchedule repeatSchedule, LocalDate exceptionDate);
}
