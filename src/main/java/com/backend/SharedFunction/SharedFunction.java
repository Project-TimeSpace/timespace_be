package com.backend.SharedFunction;

import com.backend.User.Entity.RepeatException;
import com.backend.User.Entity.RepeatSchedule;
import com.backend.User.Entity.SingleSchedule;
import com.backend.User.Repository.RepeatExceptionRepository;
import com.backend.User.Repository.RepeatScheduleRepository;
import com.backend.User.Repository.SingleScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SharedFunction {

    private final SingleScheduleRepository singleScheduleRepository;
    private final RepeatScheduleRepository repeatScheduleRepository;
    private final RepeatExceptionRepository repeatExceptionRepository;

    // Long scheduleId가 0L 일때는 새로 등록하는 일정일 때, 수정하는 상황에서는 id 넣어주고
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
            if (r.getRepeatDays().getValue() != date.getDayOfWeek().getValue()) continue;

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

    // Long RepeatId 0L 일때는 새로 등록하는 일정일 때, 수정하는 상황에서는 id 넣어주고
    /*
    public void validateRepeatScheduleOverlap(Long userId, int repeatDay, LocalTime newStart, LocalTime newEnd,
            LocalDate startDate, LocalDate endDate, Long RepeatId) {

        List<RepeatSchedule> repeats = repeatScheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, endDate, startDate);

        for (RepeatSchedule r : repeats) {
            if (RepeatId != null && r.getId().equals(RepeatId)) continue;
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
    */

    public void validateRepeatScheduleOverlap(Long userId, int repeatDay,
            LocalTime newStart, LocalTime newEnd, LocalDate startDate, LocalDate endDate, Long excludeRepeatId) {

        // 1) 발생일 리스트 계산
        List<LocalDate> occurrences = new ArrayList<>();
        LocalDate occ = startDate;
        while (occ.getDayOfWeek().getValue() != repeatDay) {
            occ = occ.plusDays(1);
        }
        while (!occ.isAfter(endDate)) {
            occurrences.add(occ);
            occ = occ.plusWeeks(1);
        }
        if (occurrences.isEmpty())
            return;

        // 2) 기존 단일 일정 한 번에 조회 & 날짜별 그룹핑
        List<SingleSchedule> singleList = singleScheduleRepository.findAllByUserIdAndDateIn(userId, occurrences);
        Map<LocalDate, List<SingleSchedule>> singleMap = singleList.stream()
                .collect(Collectors.groupingBy(SingleSchedule::getDate));

        // 3) 기존 반복 일정 전체 조회
        List<RepeatSchedule> repeats = repeatScheduleRepository
                        .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, endDate, startDate);

        // 4) 반복 예외일 한 번에 조회
        List<Long> repeatIds = repeats.stream().map(RepeatSchedule::getId).collect(Collectors.toList());
        Map<Long, Set<LocalDate>> exMap = repeatIds.isEmpty() ? Collections.emptyMap()
                : repeatExceptionRepository
                        .findAllByRepeatScheduleIdInAndExceptionDateBetween(repeatIds, startDate, endDate)
                        .stream().collect(Collectors.groupingBy(ex -> ex.getRepeatSchedule().getId(),
                                Collectors.mapping(RepeatException::getExceptionDate, Collectors.toSet())));

        // 5) 충돌 검사
        for (LocalDate date : occurrences) {
            // 5-1) 단일 일정 충돌
            for (SingleSchedule s : singleMap.getOrDefault(date, List.of())) {
                if (newStart.isBefore(s.getEndTime()) &&
                        s.getStartTime().isBefore(newEnd)) {
                    throw new IllegalArgumentException(
                            "단일 일정(id=" + s.getId() + ")과 시간이 겹칩니다: " +
                                    s.getStartTime() + "~" + s.getEndTime()
                    );
                }
            }
            // 5-2) 반복 일정 충돌
            for (RepeatSchedule r : repeats) {
                if (r.getId().equals(excludeRepeatId)) continue;
                if (r.getRepeatDays().getValue() != repeatDay) continue;

                Set<LocalDate> exceptions = exMap.getOrDefault(r.getId(), Set.of());
                if (exceptions.contains(date)) continue;

                if (newStart.isBefore(r.getEndTime()) &&
                        r.getStartTime().isBefore(newEnd)) {
                    throw new IllegalArgumentException(
                            "반복 일정(id=" + r.getId() + ")과 시간이 겹칩니다: " +
                                    r.getStartTime() + "~" + r.getEndTime()
                    );
                }
            }
        }
    }


}
