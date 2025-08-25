package com.backend.User.Service;

import com.backend.ConfigEnum.GlobalEnum.DayOfWeek;
import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.User.Error.UserScheduleErrorCode;
import com.backend.response.BusinessException;
import com.backend.shared.SharedFunction;
import com.backend.User.Dto.CreateSingleScheduleDto;
import com.backend.User.Dto.UserScheduleDto;
import com.backend.User.Entity.SingleSchedule;
import com.backend.User.Entity.User;
import com.backend.User.Repository.SingleScheduleRepository;
import com.backend.User.Repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserSingleScheduleService {

    private final SingleScheduleRepository singleScheduleRepository;
    private final UserService userService;
    private final SharedFunction sharedFunction;

    private SingleSchedule getSingleScheduleOrThrow(Long scheduleId) {
        return singleScheduleRepository.findById(scheduleId)
            .orElseThrow(() ->
                new BusinessException(UserScheduleErrorCode.SINGLE_SCHEDULE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<UserScheduleDto> getSingleSchedulesByPeriod(Long userId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);

        return singleScheduleRepository.findAllByUserIdAndDateBetween(userId, start, end)
            .stream()
            .map(UserScheduleDto::fromSingle)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UserScheduleDto> getSingleSchedulesByPeriod(Long userId, LocalDate startDate, LocalDate endDate) {

        return singleScheduleRepository.findAllByUserIdAndDateBetween(userId, startDate, endDate)
            .stream()
            .map(UserScheduleDto::fromSingle)
            .toList();
    }

    @Transactional
    public void createSingleSchedule(Long userId, CreateSingleScheduleDto dto) {
        User user = userService.getUserById(userId);

        LocalDate date      = dto.getDate();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime   = dto.getEndTime();

        // 해당 시간대에 추가 가능한지.
        sharedFunction.validateSingleScheduleOverlap(userId, date, startTime, endTime, 0L);

        int day = date.getDayOfWeek().getValue();

        SingleSchedule s = SingleSchedule.builder()
                .user(user)
                .title(dto.getTitle())
                .color(ScheduleColor.fromCode(dto.getColor()))
                .date(date)
                .day(DayOfWeek.fromValue(day))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        singleScheduleRepository.save(s);
    }

    @Transactional(readOnly = true)
    public UserScheduleDto getSingleScheduleDetail(Long userId, Long scheduleId) {
        SingleSchedule s = getSingleScheduleOrThrow(scheduleId);

        if (!s.getUser().getId().equals(userId)) {
            throw new BusinessException(UserScheduleErrorCode.FORBIDDEN_SCHEDULE_ACCESS);
        }

        return UserScheduleDto.fromSingle(s);
    }

    @Transactional
    public void updateSingleSchedule(Long userId, Long scheduleId, CreateSingleScheduleDto dto) {

        SingleSchedule s = getSingleScheduleOrThrow(scheduleId);

        if (!s.getUser().getId().equals(userId)) {
            throw new BusinessException(UserScheduleErrorCode.FORBIDDEN_SCHEDULE_ACCESS);
        }

        LocalDate finalDate = dto.getDate()      != null ? dto.getDate()      : s.getDate();
        LocalTime finalStart= dto.getStartTime() != null ? dto.getStartTime() : s.getStartTime();
        LocalTime finalEnd  = dto.getEndTime()   != null ? dto.getEndTime()   : s.getEndTime();

        sharedFunction.validateSingleScheduleOverlap(userId, finalDate, finalStart, finalEnd, scheduleId);

        if (dto.getTitle() != null)
            s.setTitle(dto.getTitle());
        if (dto.getColor() != 0   )
            s.setColor(ScheduleColor.fromCode(dto.getColor()));
        if (dto.getDate() != null) {
            s.setDate(finalDate);
            s.setDay(DayOfWeek.fromValue(finalDate.getDayOfWeek().getValue()));
        }

        s.setStartTime(finalStart);
        s.setEndTime(finalEnd);
    }

    @Transactional
    public void deleteSingleSchedule(Long userId, Long scheduleId) {
        SingleSchedule s = getSingleScheduleOrThrow(scheduleId);

        if (!s.getUser().getId().equals(userId)) {
            throw new BusinessException(UserScheduleErrorCode.FORBIDDEN_SCHEDULE_ACCESS);
        }
        singleScheduleRepository.delete(s);
    }


}
