package com.backend.User.Service;

import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.SharedFunction.SharedFunction;
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
    private final UserRepository userRepository;
    private final SharedFunction sharedFunction;

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

        return singleScheduleRepository.findAllByUserIdAndDateBetween(userId, start, end)
                .stream()
                .map(s -> UserScheduleDto.builder()
                        .id(s.getId())
                        .isRepeat(false)
                        .title(s.getTitle())
                        .color(s.getColor().getCode())
                        .date(s.getDate())
                        .day(s.getDay())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }

    // 3. 일정 추가
    // 일정 추가시 기존 일정과 겹치는지 확인하는 로직
    // ** 추후에 그룹 일정이랑 겹치는지도 확인해야함..... 복잡한데

    // 3-1
    @Transactional
    public void createSingleSchedule(Long userId, CreateSingleScheduleDto dto) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDate date      = dto.getDate();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime   = dto.getEndTime();

        // 해당 시간대에 추가 가능한지.
        sharedFunction.validateSingleScheduleOverlap(userId, date, startTime, endTime, 0L);

        SingleSchedule s = SingleSchedule.builder()
                .user(user)
                .title(dto.getTitle())
                .color(ScheduleColor.fromCode(dto.getColor()))
                .date(date)
                .day(dto.getDay())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        singleScheduleRepository.save(s);
    }

    // 3-2
    @Transactional(readOnly = true)
    public UserScheduleDto getSingleScheduleDetail(Long userId, Long scheduleId) {
        SingleSchedule s = singleScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("단일 일정을 찾을 수 없습니다."));

        if (!s.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        return UserScheduleDto.builder()
                .id(s.getId())
                .isRepeat(false)
                .title(s.getTitle())
                .color(s.getColor().getCode())
                .date(s.getDate())
                .day(s.getDay())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .build();
    }

    // 3-3. Single 일정 업데이트
    @Transactional
    public void updateSingleSchedule(Long userId, Long scheduleId, CreateSingleScheduleDto dto) {
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
        sharedFunction.validateSingleScheduleOverlap(userId, finalDate, finalStart, finalEnd, scheduleId);

        // 4) 실제 업데이트
        if (dto.getTitle() != null)
            s.setTitle(dto.getTitle());
        if (dto.getColor() != 0   )
            s.setColor(ScheduleColor.fromCode(dto.getColor()));
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

    // 3-4. Single일정 삭제
    @Transactional
    public void deleteSingleSchedule(Long userId, Long scheduleId) {
        SingleSchedule s = singleScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("단일 일정을 찾을 수 없습니다."));
        if (!s.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        singleScheduleRepository.delete(s);
    }


}
