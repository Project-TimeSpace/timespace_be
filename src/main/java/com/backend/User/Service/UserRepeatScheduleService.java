package com.backend.User.Service;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.User.Dto.CreateRepeatScheduleDto;
import com.backend.User.Dto.UserRepeatScheduleDto;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.RepeatException;
import com.backend.User.Entity.RepeatSchedule;
import com.backend.User.Entity.User;
import com.backend.User.Entity.UserCategory;
import com.backend.User.Repository.RepeatExceptionRepository;
import com.backend.User.Repository.RepeatScheduleRepository;
import com.backend.User.Repository.UserCategoryRepository;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRepeatScheduleService {

    private final RepeatScheduleRepository repeatScheduleRepository;
    private final RepeatExceptionRepository repeatExceptionRepository;
    private final UserRepository userRepository;
    private final SharedFunction sharedFunction;
    private final UserCategoryRepository userCategoryRepository;


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
                            .category(r.getCategory())
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

    // 4. Repeat Schedule CRUD

    // 4-1
    /*
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
            sharedFunction.validateSingleScheduleOverlap(userId, date, startTime, endTime, 0L);
            //sharedFunction.validateRepeatScheduleOverlap(userId, dow, startTime, endTime, startDate, endDate, null);
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
                .category(GlobalEnum.ScheduleCategory.fromCode(dto.getCategory()))
                .startDate(startDate)
                .endDate(endDate)
                .repeatDays(dow)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        repeatScheduleRepository.save(r);
    }
     */

    @Transactional
    public void createRepeatSchedule(Long userId, CreateRepeatScheduleDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate   = dto.getEndDate();
        int      dow        = dto.getRepeatDays();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime   = dto.getEndTime();

        // 1) 전체 기간중 반복일정 추가 요일에 대한 충돌 검사 — 단일+반복 모두 1회 검사
        sharedFunction.validateRepeatScheduleOverlap(userId, dow, startTime, endTime, startDate, endDate, 0L);

        UserCategory category = userCategoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // 2) 저장
        RepeatSchedule r = RepeatSchedule.builder()
                .user(user)
                .title(dto.getTitle())
                .color(dto.getColor())
                .category(category)
                .startDate(startDate)
                .endDate(endDate)
                .repeatDays(dow)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        repeatScheduleRepository.save(r);
    }


    // 4-2
    @Transactional(readOnly = true)
    public UserRepeatScheduleDto getRepeatScheduleDetail(Long userId, Long scheduleId) {
        RepeatSchedule r = repeatScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("반복 일정을 찾을 수 없습니다."));

        if (!r.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        UserCategory category = userCategoryRepository.findById(r.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        return UserRepeatScheduleDto.builder()
                .id(r.getId())
                .title(r.getTitle())
                .color(r.getColor())
                .category(r.getCategory())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .repeatDays(r.getRepeatDays())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .build();
    }

    // 4-3
    @Transactional
    public void updateRepeatSchedule(Long userId, Long scheduleId, CreateRepeatScheduleDto dto) {
        // 1) 조회 & 권한 체크
        RepeatSchedule r = repeatScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("반복 일정을 찾을 수 없습니다."));
        if (!r.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 2) 최종값 병합
        LocalDate finalStartDate = dto.getStartDate()   != null ? dto.getStartDate()   : r.getStartDate();
        LocalDate finalEndDate   = dto.getEndDate()     != null ? dto.getEndDate()     : r.getEndDate();
        int      finalDow        = dto.getRepeatDays()  != 0    ? dto.getRepeatDays()    : r.getRepeatDays();
        LocalTime finalStartTime = dto.getStartTime()   != null ? dto.getStartTime()   : r.getStartTime();
        LocalTime finalEndTime   = dto.getEndTime()     != null ? dto.getEndTime()     : r.getEndTime();

        // 3) 패턴 변경 여부 판단
        boolean patternChanged =
                dto.getStartDate()!= null || dto.getEndDate()!= null ||
                        dto.getRepeatDays()!= 0 || dto.getStartTime()!= null || dto.getEndTime() != null;

        if (patternChanged) {
            // 기존 일정과 충돌 검증 (자기 자신 제외)
            sharedFunction.validateRepeatScheduleOverlap(
                    userId,
                    finalDow,
                    finalStartTime,
                    finalEndTime,
                    finalStartDate,
                    finalEndDate,
                    scheduleId
            );
        }

        // 4) 나머지 필드 업데이트
        if (dto.getTitle()    != null) r.setTitle(dto.getTitle());
        if (dto.getColor()    != 0)    r.setColor(dto.getColor());
        if (dto.getCategory() != 0)    r.setCategory(GlobalEnum.ScheduleCategory.fromCode(dto.getCategory()));

        if (patternChanged) {
            r.setStartDate(finalStartDate);
            r.setEndDate(finalEndDate);
            r.setRepeatDays(finalDow);
            r.setStartTime(finalStartTime);
            r.setEndTime(finalEndTime);
        }
    }


    // 4-4
    @Transactional
    public void deleteRepeatSchedule(Long userId, Long repeatId) {
        RepeatSchedule r = repeatScheduleRepository.findById(repeatId)
                .orElseThrow(() -> new IllegalArgumentException("반복 일정을 찾을 수 없습니다."));
        if (!r.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        repeatScheduleRepository.delete(r);
    }

    // 4-5
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
}
