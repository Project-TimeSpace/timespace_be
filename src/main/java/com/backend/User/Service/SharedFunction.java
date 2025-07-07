package com.backend.User.Service;

import com.backend.User.Entity.RepeatSchedule;
import com.backend.User.Entity.SingleSchedule;
import com.backend.User.Repository.RepeatExceptionRepository;
import com.backend.User.Repository.RepeatScheduleRepository;
import com.backend.User.Repository.SingleScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SharedFunction {

    private final SingleScheduleRepository singleScheduleRepository;
    private final RepeatScheduleRepository repeatScheduleRepository;
    private final RepeatExceptionRepository repeatExceptionRepository;

    public void validateSingleScheduleOverlap(Long userId, LocalDate date,
            LocalTime newStart, LocalTime newEnd, Long scheduleId) {

        // 1) 같은 날짜에 이미 있는 단일 일정만 조회
        List<SingleSchedule> singleList = singleScheduleRepository.findAllByUserIdAndDate(userId, date);

        for (SingleSchedule s : singleList) {
            // 시간 겹침 검사: newStart < existingEnd && existingStart < newEnd
            if (scheduleId != 0 && s.getId() == scheduleId){
                continue;
            }
            if (newStart.isBefore(s.getEndTime()) && s.getStartTime().isBefore(newEnd)) {
                throw new IllegalArgumentException(
                        "단일 일정(id=" + s.getId() + ")과 시간이 겹칩니다: " + s.getStartTime() + "~" + s.getEndTime());
            }
        }

        // 2) 반복 일정과의 충돌 검사
        // 2-1) date를 포함하는 반복 스케줄만 조회
        List<RepeatSchedule> repeats = repeatScheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, date, date);

        for (RepeatSchedule r : repeats) {
            // 2-2) 요일이 맞지 않으면 건너뜀
            if (r.getRepeatDays() != date.getDayOfWeek().getValue()) continue;

            // 2-3) 해당 날짜가 예외일이면 충돌 검사에서 제외
            boolean isException = repeatExceptionRepository
                    .existsByRepeatScheduleAndExceptionDate(r, date);
            if (isException) continue;

            // 2-4) 시간 충돌 검사
            if (newStart.isBefore(r.getEndTime()) && r.getStartTime().isBefore(newEnd)) {
                throw new IllegalArgumentException("반복 일정(id=" + r.getId() + ")과 시간이 겹칩니다: " + r.getStartTime() + "~" + r.getEndTime());
            }
        }
    }

    public void validateRepeatScheduleOverlap(Long userId, int repeatDay, LocalTime newStart, LocalTime newEnd,
            LocalDate startDate, LocalDate endDate, Long excludeRepeatId) {

        List<RepeatSchedule> repeats = repeatScheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, endDate, startDate);

        for (RepeatSchedule r : repeats) {
            if (excludeRepeatId != null && r.getId().equals(excludeRepeatId)) continue;
            if (r.getRepeatDays() != repeatDay) continue;

            // 반복 예외는 고려하지 않음 (정의 상 반복 일정 전체 충돌 체크)
            if (newStart.isBefore(r.getEndTime()) && r.getStartTime().isBefore(newEnd)) {
                throw new IllegalArgumentException("기존 반복 일정(id=" + r.getId() + ")과 시간이 겹칩니다: " +
                        r.getStartTime() + "~" + r.getEndTime());
            }
        }

        // 단일 일정과의 충돌 확인 (날짜별로 loop)
        LocalDate occurrence = startDate;
        while (occurrence.getDayOfWeek().getValue() != repeatDay) {
            occurrence = occurrence.plusDays(1);
        }
        while (!occurrence.isAfter(endDate)) {
            validateSingleScheduleOverlap(userId, occurrence, newStart, newEnd, null);
            occurrence = occurrence.plusWeeks(1);
        }
    }

}
