package com.backend.Group.Service;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.ConfigEnum.GlobalEnum.DayOfWeek;
import com.backend.ConfigEnum.GlobalEnum.ScheduleColor;
import com.backend.Group.Dto.GroupScheduleCreateRequest;
import com.backend.Group.Dto.GroupScheduleDto;
import com.backend.Group.Entity.Group;
import com.backend.Group.Entity.GroupMembers;
import com.backend.Group.Entity.GroupSchedule;
import com.backend.Group.Entity.GroupScheduleUser;
import com.backend.Group.Repository.GroupMembersRepository;
import com.backend.Group.Repository.GroupRepository;
import com.backend.Group.Repository.GroupScheduleRepository;
import com.backend.Group.Repository.GroupScheduleUserRepository;
import com.backend.Notification.Service.NotificationService;
import com.backend.SharedFunction.SharedFunction;
import com.backend.User.Repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
    private final UserRepository userRepository;

    private final GroupMembersRepository groupMembersRepository;
    private final GroupScheduleUserRepository groupScheduleUserRepository;
    private final NotificationService notificationService;
    private final SharedFunction sharedFunction;


    public List<GroupScheduleDto> getGroupSchedules(Long groupId) {
        List<GroupSchedule> schedules = groupScheduleRepository.findByGroupId(groupId);

        return schedules.stream()
            .map(schedule -> GroupScheduleDto.builder()
                .groupId(groupId)
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .color(schedule.getColor().getCode())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public GroupScheduleDto createSchedule(Long groupId, GroupScheduleCreateRequest request) {
        // 1) 그룹 존재 체크
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("그룹이 존재하지 않습니다."));

        // 2) 일정 생성
        GroupSchedule saved = groupScheduleRepository.save(
            GroupSchedule.builder()
                .group(group)
                .title(request.getTitle())
                .color(ScheduleColor.fromCode(request.getColor()))
                .date(request.getDate())
                .day(DayOfWeek.fromValue(request.getDate().getDayOfWeek().getValue()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .createdAt(LocalDateTime.now())
                .build()
        );

        // 3) 모든 그룹 멤버(방장 포함) GSU 행을 PENDING으로 생성
        List<GroupMembers> members = groupMembersRepository.findByGroupId(groupId);
        List<GroupScheduleUser> linkRows = new ArrayList<>(members.size());
        for (GroupMembers m : members) {
            linkRows.add(
                GroupScheduleUser.builder()
                    .groupSchedule(saved)
                    .user(m.getUser())
                    .status(GlobalEnum.RequestStatus.PENDING) // 전원 PENDING
                    .build()
            );
        }
        groupScheduleUserRepository.saveAll(linkRows);

        // =====================[NOTIFICATION_BLOCK_START]=====================
        Long masterId = group.getMaster().getId();
        List<Long> notifyTargets = members.stream()
            .map(m -> m.getUser().getId())
            .toList();

        String content = String.format("그룹 일정 '%s'이(가) 생성되었습니다. 확인해 주세요.", saved.getTitle());
        notificationService.createNotifications(
            masterId,
            notifyTargets,
            GlobalEnum.NotificationType.GROUP_SCHEDULE,
            content,
            saved.getId()
        );
        // ======================[NOTIFICATION_BLOCK_END]======================

        // 4) 응답 DTO
        return GroupScheduleDto.builder()
            .groupId(groupId)
            .scheduleId(saved.getId())
            .title(saved.getTitle())
            .color(saved.getColor().getCode())
            .date(saved.getDate())
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

        // 0) 기존 값 보관
        LocalDate oldDate  = schedule.getDate();
        LocalTime oldStart = schedule.getStartTime();
        LocalTime oldEnd   = schedule.getEndTime();

        // 1) 새 값 준비
        LocalDate newDate  = request.getDate();
        LocalTime newStart = request.getStartTime();
        LocalTime newEnd   = request.getEndTime();

        // 2) 날짜/시간 변경 여부
        boolean timeChanged =
            !Objects.equals(oldDate,  newDate)  ||
                !Objects.equals(oldStart, newStart) ||
                !Objects.equals(oldEnd,   newEnd);

        // 3) 필드 반영 (day는 date에서 일관 계산)
        schedule.setTitle(request.getTitle());
        schedule.setColor(ScheduleColor.fromCode(request.getColor()));
        schedule.setDate(newDate);
        schedule.setDay(DayOfWeek.fromValue(newDate.getDayOfWeek().getValue()));
        schedule.setStartTime(newStart);
        schedule.setEndTime(newEnd);

        GroupSchedule updated = groupScheduleRepository.save(schedule);

        // 4) 날짜/시간 바뀐 경우에만 수락 초기화 + 알림
        if (timeChanged) {
            // 수락 → 대기로 초기화
            groupScheduleUserRepository.bulkResetStatus(
                scheduleId, GlobalEnum.RequestStatus.ACCEPTED, GlobalEnum.RequestStatus.PENDING);

            /*
            // =====================[NOTIFICATION_BLOCK_START]=====================
            List<Long> receivers = groupMembersRepository.findByGroupId(groupId)
                .stream()
                .map(m -> m.getUser().getId())
                .toList();

            String content = String.format("그룹 일정 '%s'의 날짜/시간이 변경되었습니다. 다시 수락해주세요.", updated.getTitle());
            notificationService.createNotifications(
                updated.getGroup().getMaster().getId(),   // sender: 방장
                receivers,                                 // 모든 멤버(방장 포함)
                GlobalEnum.NotificationType.GROUP_SCHEDULE,
                content,
                updated.getId()
            );
            // ======================[NOTIFICATION_BLOCK_END]======================
            */
        }

        // 5) 응답
        return GroupScheduleDto.builder()
            .scheduleId(updated.getId())
            .title(updated.getTitle())
            .color(updated.getColor().getCode())
            .date(updated.getDate())
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

        // =====================[NOTIFICATION_BLOCK_START]=====================
        Group group   = schedule.getGroup();
        Long masterId = group.getMaster().getId();

        String content = String.format("그룹 일정 '%s'이(가) 삭제되었습니다.", schedule.getTitle());
        // 방장 포함 모든 멤버에게 알림
        List<Long> receivers = groupMembersRepository.findByGroupId(groupId)
            .stream()
            .map(m -> m.getUser().getId())
            .toList();

        notificationService.createNotifications(
            masterId,
            receivers,
            GlobalEnum.NotificationType.GROUP_SCHEDULE,
            content,
            schedule.getId()
        );
        // ======================[NOTIFICATION_BLOCK_END]======================

        // 1) 링크 테이블(참여자 상태) 먼저 삭제 → FK 제약 안전
        groupScheduleUserRepository.deleteAllByGroupScheduleId(scheduleId);

        // 2) 실제 일정 삭제
        groupScheduleRepository.delete(schedule);
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
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .build())
                .collect(Collectors.toList());
    }


    // 아래 두개는 UserScheduleController에서 사용
    @Transactional
    public void updateMyStatus(Long userId, Long groupId, Long scheduleId, GlobalEnum.RequestStatus newStatus) {
        GroupSchedule schedule = groupScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("일정이 존재하지 않습니다."));
        if (!schedule.getGroup().getId().equals(groupId)) {
            throw new org.springframework.security.access.AccessDeniedException("그룹 접근 권한이 없습니다.");
        }

        GroupScheduleUser gsu = groupScheduleUserRepository
            .findByGroupSchedule_IdAndUser_Id(scheduleId, userId)
            .orElseGet(() -> GroupScheduleUser.builder()
                .groupSchedule(schedule)
                .user(userRepository.getReferenceById(userId))
                .status(GlobalEnum.RequestStatus.PENDING)
                .build());

        if (gsu.getStatus() == newStatus)
            return; // idempotent

        // ACCEPTED 로 바꾸는 경우에만 겹침 검증
        if (newStatus == GlobalEnum.RequestStatus.ACCEPTED) {
            sharedFunction.validateSingleScheduleOverlap(
                userId, schedule.getDate(), schedule.getStartTime(), schedule.getEndTime(), 0L
            );
            gsu.setAcceptedAt(LocalDateTime.now());
        } else {
            // PENDING/REJECTED 는 acceptedAt 초기화
            gsu.setAcceptedAt(null);
        }

        gsu.setStatus(newStatus);
        groupScheduleUserRepository.save(gsu);

    }

    @Transactional(readOnly = true)
    public List<GroupScheduleDto> getMyGroupSchedules(Long userId, String startDateStr, String endDateStr, String statusStr) {
        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end   = LocalDate.parse(endDateStr);
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end date가 start date 보다 앞일 수 없습니다.");
        }

        // status 파싱 (없거나 잘못된 값이면 null → 전체 조회)
        GlobalEnum.RequestStatus status = parseStatusNullable(statusStr);

        List<GroupScheduleUser> rows = (status == null)
            ? groupScheduleUserRepository
            .findByUser_IdAndGroupSchedule_DateBetweenOrderByGroupSchedule_DateAscGroupSchedule_StartTimeAsc(userId, start, end)
            : groupScheduleUserRepository
            .findByUser_IdAndStatusAndGroupSchedule_DateBetweenOrderByGroupSchedule_DateAscGroupSchedule_StartTimeAsc(userId, status, start, end);

        return rows.stream()
            .map(gsu -> {
                GroupSchedule gs = gsu.getGroupSchedule();
                return GroupScheduleDto.builder()
                    .groupId(gs.getGroup().getId())
                    .scheduleId(gs.getId())
                    .title(gs.getTitle())
                    .color(gs.getColor() != null ? gs.getColor().getCode() : 0)
                    .date(gs.getDate())
                    .startTime(gs.getStartTime())
                    .endTime(gs.getEndTime())
                    // 라벨을 쓰고 싶다면 gsu.getStatus().getLabel() 로 교체
                    .status(gsu.getStatus().name())
                    .build();
            })
            .toList();
    }

    /** status 문자열을 옵션으로 파싱. 유효하지 않으면 null 반환(=전체) */
    private GlobalEnum.RequestStatus parseStatusNullable(String raw) {
        if (raw.equals("ALL") || raw == null || raw.isBlank())
            return null;

        String s = raw.trim();

        // 1) 영문 상수명
        switch (s.toUpperCase(Locale.ROOT)) {
            case "PENDING":  return GlobalEnum.RequestStatus.PENDING;
            case "ACCEPTED": return GlobalEnum.RequestStatus.ACCEPTED;
            case "REJECTED": return GlobalEnum.RequestStatus.REJECTED;
        }

        // 2) 한글 라벨 지원 (대기/수락/거절)
        switch (s) {
            case "대기":  return GlobalEnum.RequestStatus.PENDING;
            case "수락":  return GlobalEnum.RequestStatus.ACCEPTED;
            case "거절":  return GlobalEnum.RequestStatus.REJECTED;
        }

        // 3) 코드 숫자 지원 (1/2/3)
        if (s.chars().allMatch(Character::isDigit)) {
            int code = Integer.parseInt(s);
            for (GlobalEnum.RequestStatus rs : GlobalEnum.RequestStatus.values()) {
                if (rs.getCode() == code) return rs;
            }
        }

        // 전부 실패 → 전체 조회
        return null;

        // (원하면 400을 주고 싶을 때)
        // throw new IllegalArgumentException("status는 PENDING|ACCEPTED|REJECTED|1|2|3|대기|수락|거절 중 하나여야 합니다.");
    }



}
