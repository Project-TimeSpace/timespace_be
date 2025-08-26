// com.backend.group.Repository.GroupScheduleUserRepository.java
package com.backend.group.Repository;

import com.backend.configenum.GlobalEnum;
import com.backend.group.Entity.GroupScheduleUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GroupScheduleUserRepository extends JpaRepository<GroupScheduleUser, Long> {

	Optional<GroupScheduleUser> findByGroupSchedule_IdAndUser_Id(Long scheduleId, Long userId);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
        update GroupScheduleUser g
           set g.status = :to, g.acceptedAt = null
         where g.groupSchedule.id = :scheduleId
           and g.status = :from
        """)
	int bulkResetStatus(
		@Param("scheduleId") Long scheduleId,
		@Param("from") GlobalEnum.RequestStatus from,
		@Param("to") GlobalEnum.RequestStatus to
	);

	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from GroupScheduleUser g where g.groupSchedule.id = :scheduleId")
	int deleteAllByGroupScheduleId(@Param("scheduleId") Long scheduleId);

	@EntityGraph(attributePaths = {"groupSchedule", "groupSchedule.group"})
	List<GroupScheduleUser> findByUser_IdAndGroupSchedule_DateBetweenOrderByGroupSchedule_DateAscGroupSchedule_StartTimeAsc(
		Long userId, LocalDate start, LocalDate end);

	@EntityGraph(attributePaths = {"groupSchedule", "groupSchedule.group"})
	List<GroupScheduleUser> findByUser_IdAndStatusAndGroupSchedule_DateBetweenOrderByGroupSchedule_DateAscGroupSchedule_StartTimeAsc(
		Long userId, GlobalEnum.RequestStatus status, LocalDate start, LocalDate end);

	boolean existsByUser_IdAndStatusAndGroupSchedule_DateAndGroupSchedule_StartTimeLessThanAndGroupSchedule_EndTimeGreaterThan(
		Long userId, GlobalEnum.RequestStatus status, LocalDate date, LocalTime newEnd, LocalTime newStart);

	@Query("""
    select (count(gsu) > 0)
    from GroupScheduleUser gsu
    where gsu.user.id = :userId
      and gsu.status = :status
      and gsu.groupSchedule.date in :dates
      and gsu.groupSchedule.startTime < :end
      and gsu.groupSchedule.endTime > :start
	""")
	boolean existsAcceptedOverlapInDates(
		Long userId,
		com.backend.configenum.GlobalEnum.RequestStatus status,
		List<LocalDate> dates,
		LocalTime start,    // newStart
		LocalTime end      // newEnd
	);
}
