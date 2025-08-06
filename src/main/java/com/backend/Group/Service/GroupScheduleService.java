package com.backend.Group.Service;

import com.backend.ConfigEnum.GlobalEnum.DayOfWeek;
import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.Group.Dto.GroupScheduleCreateRequest;
import com.backend.Group.Dto.GroupScheduleDto;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupSchedule;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Group.Repository.GroupScheduleRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupScheduleService {

    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberService groupMemberService;


    @Transactional
    public GroupScheduleDto createSchedule(Long groupId, GroupScheduleCreateRequest request) {
        // 1) 그룹 존재 체크
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));

        int day = request.getDate().getDayOfWeek().getValue();
        // 2) 엔티티 생성
        GroupSchedule schedule = GroupSchedule.builder()
                .group(group)
                .title(request.getTitle())
                .color(ScheduleColor.fromCode(request.getColor()))
                .date(request.getDate())
                .day(DayOfWeek.fromValue(day))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        // 3) 저장
        GroupSchedule saved = groupScheduleRepository.save(schedule);

        // 4) DTO 변환 후 반환
        return GroupScheduleDto.builder()
                .scheduleId(saved.getId())
                .title(saved.getTitle())
                .color(saved.getColor().getCode())
                .date(saved.getDate())
                .day(saved.getDay().getValue())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .build();
    }

    @Transactional
    public GroupScheduleDto updateSchedule(Long groupId, Long scheduleId, GroupScheduleCreateRequest request) {
        GroupSchedule schedule = groupScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("수정할 일정이 존재하지 않습니다."));
        if (!schedule.getGroup().getId().equals(groupId)) {
            throw new AccessDeniedException("그룹 접근 권한이 없습니다.");
        }

        schedule.setTitle(request.getTitle());
        schedule.setColor(ScheduleColor.fromCode(request.getColor()));
        schedule.setDate(request.getDate());
        schedule.setDay(DayOfWeek.fromValue(request.getDay()));
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        GroupSchedule updated = groupScheduleRepository.save(schedule);

        return GroupScheduleDto.builder()
                .scheduleId(updated.getId())
                .title(updated.getTitle())
                .color(updated.getColor().getCode())
                .date(updated.getDate())
                .day(updated.getDay().getValue())
                .startTime(updated.getStartTime())
                .endTime(updated.getEndTime())
                .build();
    }

    @Transactional
    public void deleteSchedule(Long groupId, Long scheduleId) {
        GroupSchedule schedule = groupScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("삭제할 일정이 존재하지 않습니다."));
        if (!schedule.getGroup().getId().equals(groupId)) {
            throw new AccessDeniedException("그룹 접근 권한이 없습니다.");
        }
        groupScheduleRepository.delete(schedule);
    }

    public List<GroupScheduleDto> getGroupSchedules(Long groupId, Long userId) {
        List<GroupSchedule> schedules = groupScheduleRepository.findByGroupId(groupId);

        return schedules.stream()
                .map(schedule -> GroupScheduleDto.builder()
                        .groupId(groupId)
                        .scheduleId(schedule.getId())
                        .title(schedule.getTitle())
                        .color(schedule.getColor().getCode())
                        .date(schedule.getDate())
                        .day(schedule.getDay().getValue())  // 이미 int 값으로 저장되어 있다면 그대로 사용
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }

    public List<GroupScheduleDto> getGroupSchedulesByPeriod(Long userId, String startDateStr, String endDateStr) {
        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end = LocalDate.parse(endDateStr);

        // 1. 유저가 속한 그룹 ID 목록 조회
        List<Long> groupIds = groupMemberService.getGroupIdsByUserId(userId);

        if (groupIds.isEmpty()) return Collections.emptyList();

        // 2. 기간 내의 그룹 일정 조회
        List<GroupSchedule> schedules = groupScheduleRepository.findByGroupIdInAndDateBetween(groupIds, start, end);

        // 3. DTO로 변환
        return schedules.stream()
                .map(schedule -> GroupScheduleDto.builder()
                    .groupId(schedule.getGroup().getId())
                    .scheduleId(schedule.getId())
                    .title(schedule.getTitle())
                    .color(schedule.getColor().getCode())
                    .date(schedule.getDate())
                    .day(schedule.getDay().getValue())
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .build())
                .collect(Collectors.toList());
    }

}
