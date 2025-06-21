package com.backend.User.Service;

import com.backend.Config.GlobalEnum;
import com.backend.User.Dto.CreateRepeatScheduleDto;
import com.backend.User.Dto.CreateSingleScheduleDto;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.RepeatException;
import com.backend.User.Entity.RepeatSchedule;
import com.backend.User.Entity.SingleSchedule;
import com.backend.User.Entity.User;
import com.backend.User.Repository.RepeatExceptionRepository;
import com.backend.User.Repository.RepeatScheduleRepository;
import com.backend.User.Repository.SingleScheduleRepository;
import com.backend.User.Repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final SingleScheduleRepository singleScheduleRepository;
    private final RepeatScheduleRepository repeatScheduleRepository;
    private final RepeatExceptionRepository repeatExceptionRepository;
    private final UserRepository userRepository;

    // 1. 전체 일정 조회
    public Object getAllSchedules(Long userId) {
        // TODO: 단일 + 반복 일정 전체 조회 로직
        return null;
    }

    // 2-1. 특정기간의 단일 일정 조회
    @Transactional(readOnly = true)
    public List<UserScheduleDto> getSingleSchedulesByPeriod(Long userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);

        return singleScheduleRepository
                .findAllByUserIdAndDateBetween(userId, start, end)
                .stream()
                .map(s -> UserScheduleDto.builder()
                        .id(s.getId())
                        .isRepeat(false)
                        .title(s.getTitle())
                        .color(s.getColor())
                        .date(s.getDate())
                        .day(s.getDay())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }

    // 2-2. 특정기간의 반복 일정 조회
    @Transactional(readOnly = true)
    public List<UserScheduleDto> getRepeatSchedulesByPeriod(Long userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);

        // 1) 기간 겹치는 반복 스케줄만
        List<RepeatSchedule> repeats = repeatScheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, end, start);

        // 2) 관련 예외일자만 DB에서 얻기
        List<Long> repeatIds = repeats.stream()
                .map(RepeatSchedule::getId)
                .collect(Collectors.toList());

        Map<Long, Set<LocalDate>> exMap = Collections.emptyMap();
        if (!repeatIds.isEmpty()) {
            List<RepeatException> exceptions = repeatExceptionRepository.
                    findAllByRepeatScheduleIdInAndExceptionDateBetween(repeatIds, start, end);

            exMap = exceptions.stream().collect(Collectors.groupingBy(
                    ex -> ex.getRepeatSchedule().getId(),            // ← 람다로 ID 추출, Entity에 Join을 써서 이렇게 처리함
                    Collectors.mapping(RepeatException::getExceptionDate, Collectors.toSet())
            ));
        }

        // 3) DB에 저장된 startDate를 첫 발생일로 점프하면서 주 단위 루프
        List<UserScheduleDto> result = new ArrayList<>();
        for (RepeatSchedule r : repeats) {
            Set<LocalDate> exceptionDates = exMap.getOrDefault(r.getId(), Collections.emptySet());

            LocalDate applyStart = r.getStartDate().isAfter(start) ? r.getStartDate() : start;
            LocalDate applyEnd   = r.getEndDate().isBefore(end)    ? r.getEndDate()   : end;
            if (applyStart.isAfter(applyEnd)) continue;

            LocalDate occurrence = r.getStartDate();
            // applyStart 이전이면 주 단위로 점프
            while (occurrence.isBefore(applyStart)) {
                occurrence = occurrence.plusWeeks(1);
            }

            // 주 단위로 발행, 예외날짜 제거
            while (!occurrence.isAfter(applyEnd)) {
                if (!exceptionDates.contains(occurrence)) {
                    result.add(UserScheduleDto.builder()
                            .id(r.getId())
                            .isRepeat(true)
                            .title(r.getTitle())
                            .color(r.getColor())
                            .date(occurrence)
                            .day(r.getRepeatDays())
                            .startTime(r.getStartTime())
                            .endTime(r.getEndTime())
                            .build());
                }
                occurrence = occurrence.plusWeeks(1);
            }
        }

        return result;
    }

    // 3. 일정 추가
    // 일정 추가시 기존 일정과 겹치는지 확인하는 로직
    // ** 추후에 그룹 일정이랑 겹치는지도 확인해야함..... 복잡한데
    private void validateSingleOverlap(Long userId, LocalDate date, LocalTime newStart, LocalTime newEnd, Long scheduleId) {
        // 0) 수정할 때 본인인지 확인해야함.
        if(scheduleId == 0){}

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
                throw new IllegalArgumentException(
                        "반복 일정(id=" + r.getId() + ")과 시간이 겹칩니다: " +
                                r.getStartTime() + "~" + r.getEndTime()
                );
            }
        }
    }

    @Transactional
    public void createSingleSchedule(Long userId, CreateSingleScheduleDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDate date      = dto.getDate();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime   = dto.getEndTime();

        // 해당 시간대에 추가 가능한지.
        validateSingleOverlap(userId, date, startTime, endTime, 0L);

        SingleSchedule s = SingleSchedule.builder()
                .user(user)
                .title(dto.getTitle())
                .color(dto.getColor())
                .date(date)
                .day(dto.getDay())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        singleScheduleRepository.save(s);
    }

    @Transactional
    public void createRepeatSchedule(Long userId, CreateRepeatScheduleDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate   = dto.getEndDate();
        int      dow        = dto.getRepeatDays();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime   = dto.getEndTime();

        // 1) startDate~endDate 사이에 발생하는 모든 날짜 계산
        List<LocalDate> occurrences = new ArrayList<>();
        LocalDate occ = startDate;
        // 첫 반복일(요일)로 이동
        while (occ.getDayOfWeek().getValue() != dow) {
            occ = occ.plusDays(1);
        }
        while (!occ.isAfter(endDate)) {
            occurrences.add(occ);
            occ = occ.plusWeeks(1);
        }

        // 2) 각 발생일에 단일 일정 충돌 검사 (예외일 제외 되도록 했음.)
        for (LocalDate date : occurrences) {
            validateSingleOverlap(userId, date, startTime, endTime, 0L);
        }

        // 3) 기존 반복 스케줄 간 충돌 검사 (같은 요일)
        List<RepeatSchedule> existing = repeatScheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, endDate, startDate);
        for (RepeatSchedule r : existing) {
            if (r.getRepeatDays() == dow) {
                // 예외일자면 건너뜀
                boolean isException = repeatExceptionRepository
                        .existsByRepeatScheduleAndExceptionDate(r, r.getStartDate());
                if (isException) continue;

                // 시간대 겹침 검사
                if (startTime.isBefore(r.getEndTime()) && r.getStartTime().isBefore(endTime)) {
                    throw new IllegalArgumentException(
                            "기존 반복 일정(id=" + r.getId() + ")과 시간이 겹칩니다: " +
                                    r.getStartTime() + "~" + r.getEndTime()
                    );
                }
            }
        }

        RepeatSchedule r = RepeatSchedule.builder()
                .user(user)
                .title(dto.getTitle())
                .color(dto.getColor())
                .startDate(startDate)
                .endDate(endDate)
                .repeatDays(dow)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        repeatScheduleRepository.save(r);
    }

    // 4. 일정 수정 함수
    // 4-1. SingleSchedule 수정하기
    @Transactional
    public void updateSingleSchedule(Long userId, Long scheduleId,
            CreateSingleScheduleDto dto) {
        // 1) 엔티티 로드 및 권한 체크
        SingleSchedule s = singleScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("단일 일정을 찾을 수 없습니다."));
        if (!s.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("사용자가 해당 일정을 수정할 권한이 없습니다.");
        }

        // 2) “최종값” 계산: DTO에 null인 경우 기존 값 유지
        LocalDate finalDate = dto.getDate()      != null ? dto.getDate()      : s.getDate();
        LocalTime finalStart= dto.getStartTime() != null ? dto.getStartTime() : s.getStartTime();
        LocalTime finalEnd  = dto.getEndTime()   != null ? dto.getEndTime()   : s.getEndTime();

        // 3) 충돌 검사 (자기 자신을 제외하도록 scheduleId 넘김)
        validateSingleOverlap(userId, finalDate, finalStart, finalEnd, scheduleId);

        // 4) 실제 업데이트
        if (dto.getTitle() != null)
            s.setTitle(dto.getTitle());
        if (dto.getColor() != 0   )
            s.setColor(dto.getColor());
        if (dto.getDate() != null) {
            s.setDate(finalDate);
            s.setDay(dto.getDay());  // dto.getDay()도 null 체크 필요 or default→s.getDay()
        }
        if (dto.getDay() != 0 ) s.setDay(dto.getDay());
        // start/end 는 이미 검증했으니 바로 덮어쓰기
        s.setStartTime(finalStart);
        s.setEndTime(finalEnd);
        // JPA 변경 감지로 자동 커밋
    }


    // 5. 일정 삭제
    @Transactional
    public void deleteSingleSchedule(Long userId, Long scheduleId) {
        SingleSchedule s = singleScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("단일 일정을 찾을 수 없습니다."));
        if (!s.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        singleScheduleRepository.delete(s);
    }

    @Transactional
    public void deleteRepeatOccurrence(Long userId, Long repeatId, LocalDate date) {
        RepeatSchedule r = repeatScheduleRepository.findById(repeatId)
                .orElseThrow(() -> new IllegalArgumentException("반복 일정을 찾을 수 없습니다."));
        if (!r.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        if (date.isBefore(r.getStartDate()) || date.isAfter(r.getEndDate())) {
            throw new IllegalArgumentException("삭제하려는 날짜가 스케줄 기간에 포함되지 않습니다.");
        }
        boolean exists = repeatExceptionRepository
                .existsByRepeatScheduleAndExceptionDate(r, date);
        if (exists) {
            throw new IllegalArgumentException("이미 예외 처리된 날짜입니다: " + date);
        }
        RepeatException ex = RepeatException.builder()
                .repeatSchedule(r)
                .exceptionDate(date)
                .build();
        repeatExceptionRepository.save(ex);
    }

    @Transactional
    public void deleteRepeatSchedule(Long userId, Long repeatId) {
        RepeatSchedule r = repeatScheduleRepository.findById(repeatId)
                .orElseThrow(() -> new IllegalArgumentException("반복 일정을 찾을 수 없습니다."));
        if (!r.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        repeatScheduleRepository.delete(r);
    }
}
