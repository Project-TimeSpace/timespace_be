package com.backend.Group.Service;

import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.Group.Dto.GroupScheduleCreateRequest;
import com.backend.Group.Dto.GroupScheduleDto;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupSchedule;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Group.Repository.GroupScheduleRepository;
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


    @Transactional
    public GroupScheduleDto createSchedule(Long groupId, GroupScheduleCreateRequest request) {
        // 1) 그룹 존재 체크
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));

        // 2) 엔티티 생성
        GroupSchedule schedule = GroupSchedule.builder()
                .group(group)
                .title(request.getTitle())
                .color(request.getColor())
                .date(request.getDate())
                .day(request.getDay())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        // 3) 저장
        GroupSchedule saved = groupScheduleRepository.save(schedule);

        // 4) DTO 변환 후 반환
        return GroupScheduleDto.builder()
                .scheduleId(saved.getId())
                .title(saved.getTitle())
                .color(saved.getColor())
                .date(saved.getDate())
                .day(saved.getDay())
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
        schedule.setColor(request.getColor());
        schedule.setDate(request.getDate());
        schedule.setDay(request.getDay());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        GroupSchedule updated = groupScheduleRepository.save(schedule);

        return GroupScheduleDto.builder()
                .scheduleId(updated.getId())
                .title(updated.getTitle())
                .color(updated.getColor())
                .date(updated.getDate())
                .day(updated.getDay())
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
}
