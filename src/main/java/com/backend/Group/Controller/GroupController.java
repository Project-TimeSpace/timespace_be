package com.backend.Group.Controller;

import com.backend.Converge.ConvergedScheduleDto;
import com.backend.Converge.ScheduleConverge;
import com.backend.Group.Dto.GroupCreateRequestDto;
import com.backend.Group.Dto.GroupDto;
import com.backend.Group.Dto.GroupInfoDto;
import com.backend.Group.Dto.GroupMemberDto;
import com.backend.Group.Dto.GroupScheduleDto;
import com.backend.Group.Dto.GroupSummaryDto;
import com.backend.Group.Service.GroupMemberService;
import com.backend.Group.Service.GroupScheduleService;
import com.backend.Group.Service.GroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "4. 그룹 관련 api")
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupScheduleService groupScheduleService;
    private final ScheduleConverge scheduleConverge;

    // 1. 그룹 목록 조회 (이름, 카테고리, 인원수, 사진 포함)
    @GetMapping("/list")
    public ResponseEntity<List<GroupSummaryDto>> getUserGroups(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<GroupSummaryDto> groups = groupService.getGroupsByUserId(userId);
        return ResponseEntity.ok(groups);
    }

    // 2. 그룹 정보 조회 (사이드바 상단 정보)
    @GetMapping("/{groupId}/info")
    public ResponseEntity<GroupInfoDto> getGroupInfo(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        GroupInfoDto info = groupService.getGroupInfo(userId, groupId);
        return ResponseEntity.ok(info);
    }

    // 3. 그룹 추가
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GroupCreateRequestDto request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.createGroup(userId, request);
        return ResponseEntity.ok("그룹이 생성되었습니다");
    }

    // 4. 그룹 멤버 조회 (사이드바 하단 목록)
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMemberDto> members = groupMemberService.getGroupMembers(groupId);
        return ResponseEntity.ok(members);
    }

    // 5. 그룹 캘린더 병합 조회 (기간 + 멤버 ID 리스트)
    @GetMapping("/{groupId}/calendar")
    public ResponseEntity<List<ConvergedScheduleDto>> getGroupCalendar(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long groupId,
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam List<Long> memberIds) {
        Long userId = Long.parseLong(userDetails.getUsername());

        // 그룹 소속 여부 체크
        boolean isMember = groupMemberService.isUserInGroup(groupId, userId);
        if (!isMember) {
            throw new AccessDeniedException("그룹 접근 권한이 없습니다.");
        }

        // 병합된 일정 가져오기
        List<ConvergedScheduleDto> merged = scheduleConverge.convergeSchedules(memberIds, startDate, endDate);
        return ResponseEntity.ok(merged);
    }

    // 6. 그룹 초대 - 이메일 방식
    @PostMapping("/{groupId}/invite/email")
    public ResponseEntity<String> inviteByEmail(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @RequestParam String email) {
        Long inviterId = Long.parseLong(userDetails.getUsername());
        groupMemberService.inviteByEmail(inviterId, groupId, email);
        return ResponseEntity.ok("이메일 초대가 완료되었습니다.");
    }

    // 7. 그룹 초대 - 친구 목록 기반
    @PostMapping("/{groupId}/invite/friend")
    public ResponseEntity<String> inviteFriend(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @RequestParam Long friendId) {
        Long inviterId = Long.parseLong(userDetails.getUsername());
        groupMemberService.inviteFriend(inviterId, groupId, friendId);
        return ResponseEntity.ok("친구를 그룹에 초대했습니다.");
    }


}


