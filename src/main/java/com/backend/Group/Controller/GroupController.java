package com.backend.Group.Controller;

import com.backend.Group.Dto.GroupCalendarRequestDto;
import com.backend.Group.Dto.GroupScheduleCreateRequest;
import com.backend.Group.Dto.GroupScheduleDto;
import com.backend.SharedFunction.Converge.ConvergedScheduleDto;
import com.backend.SharedFunction.Converge.ScheduleConverge;
import com.backend.Group.Dto.GroupCreateRequestDto;
import com.backend.Group.Dto.GroupInfoDto;
import com.backend.Group.Dto.GroupMemberDto;
import com.backend.Group.Dto.GroupSummaryDto;
import com.backend.Group.Service.GroupMemberService;
import com.backend.Group.Service.GroupScheduleService;
import com.backend.Group.Service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "4-1. 그룹 관련 api")
@PreAuthorize("hasRole('User')")
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupScheduleService groupScheduleService;
    private final ScheduleConverge scheduleConverge;


    // ----------------- Group Service -----------
    @Operation(summary = "1. 그룹 목록 조회", description = "회원이 속한 그룹 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<GroupSummaryDto>> getUserGroups(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(groupService.getGroupsByUserId(userId));
    }

    @Operation(summary = "2. 그룹 정보 조회", description = "사이드바 상단용 그룹 정보를 조회합니다.")
    @GetMapping("/{groupId}/info")
    public ResponseEntity<GroupInfoDto> getGroupInfo(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupMemberService.isUserInGroup(groupId, userId);
        return ResponseEntity.ok(groupService.getGroupInfo(userId, groupId));
    }

    @Operation(summary = "3. 그룹 생성", description = "새로운 그룹을 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GroupCreateRequestDto request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.createGroup(userId, request);
        return ResponseEntity.ok("그룹이 생성되었습니다");
    }

    @Operation(summary = "4. 그룹 멤버 조회", description = "사이드바 하단용 그룹 멤버 목록을 조회합니다.")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupMemberService.isUserInGroup(groupId, userId);
        return ResponseEntity.ok(groupMemberService.getGroupMembers(groupId));
    }

    @Operation(summary = "5. 그룹 나가기", description = "그룹 마스터는, change-master 이후에 나갈 수 있습니다.")
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        // 그룹 멤버 권한 확인 후 탈퇴 처리
        groupMemberService.isUserInGroup(groupId, userId);
        groupMemberService.leaveGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    // -------------- Group Schedule Service -----------
    @Operation(summary = "6. 그룹 멤버의 병합된 캘린더 조회", description = "기간과 멤버 목록을 기반으로 병합된 그룹 캘린더 일정을 조회합니다.")
    @GetMapping("/{groupId}/calendar")
    public ResponseEntity<List<ConvergedScheduleDto>> getGroupCalendar(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId,
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam List<Long> memberIds) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupMemberService.isUserInGroup(groupId, userId);
        List<ConvergedScheduleDto> listreturn= scheduleConverge.convergeSchedules(memberIds, startDate, endDate);
        return ResponseEntity.ok(listreturn);
    }

    @Operation(summary = "7. 그룹 스케줄 가져오기", description = "해당 그룹에서 추가된 모든 일정을 Get합니다.")
    @GetMapping("/{groupId}/schedule")
    public ResponseEntity<List<GroupScheduleDto>> getGroupSchedules(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId){
        Long userId = Long.parseLong(userDetails.getUsername());
        groupMemberService.isUserInGroup(groupId, userId);
        List<GroupScheduleDto> schedules = groupScheduleService.getGroupSchedules(groupId, userId);
        return ResponseEntity.ok(schedules);
    }
}

