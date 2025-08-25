package com.backend.User.Service;

import com.backend.ConfigEnum.GlobalEnum.DayOfWeek;
import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.User.Error.UserScheduleErrorCode;
import com.backend.response.BusinessException;
import com.backend.shared.SharedFunction;
import com.backend.User.Dto.CreateRepeatScheduleDto;
import com.backend.User.Dto.UserRepeatScheduleDto;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.RepeatException;
import com.backend.User.Entity.RepeatSchedule;
import com.backend.User.Entity.User;
import com.backend.User.Repository.RepeatExceptionRepository;
import com.backend.User.Repository.RepeatScheduleRepository;
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

    private final UserService userService;
    private final SharedFunction sharedFunction;

    private RepeatSchedule getRepeatScheduleOrThrow(Long scheduleId) {
        return repeatScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new BusinessException(UserScheduleErrorCode.REPEAT_SCHEDULE_NOT_FOUND));
    }

    private void assertOwner(RepeatSchedule r, Long userId) {
        if (!r.getUser().getId().equals(userId)) {
            throw new BusinessException(UserScheduleErrorCode.SCHEDULE_ACCESS_DENIED);
        }
    }

    @Transactional(readOnly = true)
    public List<UserScheduleDto> getRepeatSchedulesByPeriod(Long userId, LocalDate start, LocalDate end) {
        // 1) 기간 겹치는 반복 스케줄만
        List<RepeatSchedule> repeats = repeatScheduleRepository
            .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, end, start);

        // 2) 관련 일자만 DB에서 얻기
        List<Long> repeatIds = repeats.stream()
            .map(RepeatSchedule::getId)
            .collect(Collectors.toList());

        Map<Long, Set<LocalDate>> exMap = Collections.emptyMap();
        if (!repeatIds.isEmpty()) {
            List<RepeatException> exceptions = repeatExceptionRepository.
                findAllByRepeatScheduleIdInAndExceptionDateBetween(repeatIds, start, end);

            exMap = exceptions.stream().collect(Collectors.groupingBy(
                ex -> ex.getRepeatSchedule().getId(),
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
            while (occurrence.isBefore(applyStart)) {
                occurrence = occurrence.plusWeeks(1);
            }

            // 주 단위로 발행, 예외날짜 제거
            while (!occurrence.isAfter(applyEnd)) {
                if (!exceptionDates.contains(occurrence)) {
                    result.add(UserScheduleDto.fromRepeat(r, occurrence));
                }
                occurrence = occurrence.plusWeeks(1);
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<UserScheduleDto> getRepeatSchedulesByPeriod(Long userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);

        // 1) 기간 겹치는 반복 스케줄만
        List<RepeatSchedule> repeats = repeatScheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, end, start);

        // 2) 관련 일자만 DB에서 얻기
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
                    result.add(UserScheduleDto.fromRepeat(r, occurrence));
                }
                occurrence = occurrence.plusWeeks(1);
            }
        }

        return result;
    }

    @Transactional
    public void createRepeatSchedule(Long userId, CreateRepeatScheduleDto dto) {
        User user = userService.getUserById(userId);

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate   = dto.getEndDate();
        int      dow        = dto.getRepeatDays();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime   = dto.getEndTime();

        if (startDate == null || endDate == null || startTime == null || endTime == null) {
            throw new BusinessException(UserScheduleErrorCode.MISSING_REQUIRED_FIELDS);
        }

        sharedFunction.validateRepeatScheduleOverlap(userId, dow, startTime, endTime, startDate, endDate, 0L);

        RepeatSchedule r = RepeatSchedule.builder()
                .user(user)
                .title(dto.getTitle())
                .color(ScheduleColor.fromCode(dto.getColor()))
                .startDate(startDate)
                .endDate(endDate)
                .repeatDays(DayOfWeek.fromValue(dow))
                .startTime(startTime)
                .endTime(endTime)
                .build();
        repeatScheduleRepository.save(r);
    }

    @Transactional(readOnly = true)
    public UserRepeatScheduleDto getRepeatScheduleDetail(Long userId, Long scheduleId) {
        RepeatSchedule r = getRepeatScheduleOrThrow(scheduleId);
        assertOwner(r, userId);

        return UserRepeatScheduleDto.from(r);
    }

    // 4-3
    @Transactional
    public void updateRepeatSchedule(Long userId, Long scheduleId, CreateRepeatScheduleDto dto) {
        RepeatSchedule r = getRepeatScheduleOrThrow(scheduleId);
        assertOwner(r, userId);

        // 2) 최종값 병합
        LocalDate finalStartDate = dto.getStartDate()   != null ? dto.getStartDate()   : r.getStartDate();
        LocalDate finalEndDate   = dto.getEndDate()     != null ? dto.getEndDate()     : r.getEndDate();
        int      finalDow        = dto.getRepeatDays()  != 0    ? dto.getRepeatDays()    : r.getRepeatDays().getValue();
        LocalTime finalStartTime = dto.getStartTime()   != null ? dto.getStartTime()   : r.getStartTime();
        LocalTime finalEndTime   = dto.getEndTime()     != null ? dto.getEndTime()     : r.getEndTime();

        // 3) 패턴 변경 여부 판단
        boolean patternChanged = dto.getStartDate()!= null || dto.getEndDate()!= null ||
                        dto.getRepeatDays()!= 0 || dto.getStartTime()!= null || dto.getEndTime() != null;

        if (patternChanged) {
            sharedFunction.validateRepeatScheduleOverlap(userId, finalDow, finalStartTime,
                    finalEndTime, finalStartDate, finalEndDate, scheduleId);
        }

        // 4) 나머지 필드 업데이트
        if (dto.getTitle()    != null) r.setTitle(dto.getTitle());
        if (dto.getColor()    != 0)    r.setColor(ScheduleColor.fromCode(dto.getColor()));
        if (patternChanged) {
            r.setStartDate(finalStartDate);
            r.setEndDate(finalEndDate);
            r.setRepeatDays(DayOfWeek.fromValue(finalDow));
            r.setStartTime(finalStartTime);
            r.setEndTime(finalEndTime);
        }
    }


    // 4-4
    @Transactional
    public void deleteRepeatSchedule(Long userId, Long repeatId) {
        RepeatSchedule r = getRepeatScheduleOrThrow(repeatId);
        assertOwner(r, userId);
        repeatScheduleRepository.delete(r);
    }

    // 4-5
    @Transactional
    public void deleteRepeatOccurrence(Long userId, Long repeatId, LocalDate date) {
        RepeatSchedule r = repeatScheduleRepository.findById(repeatId)
                .orElseThrow(() -> new IllegalArgumentException("반복 일정을 찾을 수 없습니다."));
        assertOwner(r, userId);

        if (date.isBefore(r.getStartDate()) || date.isAfter(r.getEndDate())) {
            throw new BusinessException(UserScheduleErrorCode.OCCURRENCE_DATE_OUT_OF_RANGE);
        }
        boolean exists = repeatExceptionRepository.existsByRepeatScheduleAndExceptionDate(r, date);
        if (exists) {
            throw new BusinessException(UserScheduleErrorCode.REPEAT_OCCURRENCE_ALREADY_EXCEPTION);
        }

        RepeatException ex = RepeatException.builder()
                .repeatSchedule(r)
                .exceptionDate(date)
                .build();
        repeatExceptionRepository.save(ex);
    }
}
