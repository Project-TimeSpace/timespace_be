package com.backend.User.Repository;

import com.backend.User.Entity.RepeatException;
import com.backend.User.Entity.RepeatSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatExceptionRepository extends JpaRepository<RepeatException, Long> {
    List<RepeatException> findAllByRepeatScheduleId(Long repeatScheduleId);

    List<RepeatException> findAllByRepeatScheduleIdInAndExceptionDateBetween(List<Long> repeatScheduleIds, LocalDate start, LocalDate end);
    boolean existsByRepeatScheduleAndExceptionDate(RepeatSchedule repeatSchedule, LocalDate exceptionDate);
}
