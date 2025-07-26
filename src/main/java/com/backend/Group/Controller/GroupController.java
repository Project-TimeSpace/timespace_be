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
@Tag(name = "4. 그룹 관련 api")
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupScheduleService groupScheduleService;
    private final ScheduleConverge scheduleConverge;

    /*
    1. 그룹 목록 조회: 회원이 속한 그룹 목록을 조회 (GET /api/v1/group/list)
    2. 그룹 정보 조회: 그룹 상세 정보 조회 (GET /api/v1/group/{groupId}/info)
    3. 그룹 생성: 새로운 그룹 생성 (POST /api/v1/group/create)
    //--
    4. 그룹 멤버 조회: 그룹 멤버 목록 조회 (GET /api/v1/group/{groupId}/members)
    5. 그룹 이메일 초대: 이메일을 통한 그룹 멤버 초대 (POST /api/v1/group/{groupId}/invite/email)
    6. 그룹 친구 초대: 친구 ID를 통한 그룹 멤버 초대 (POST /api/v1/group/{groupId}/invite/friend)
    7. 방장 넘기기
    8. 그룹 나가기
    //--
    9. 그룹 캘린더 조회: 멤버 일정 병합 조회 (POST /api/v1/group/{groupId}/calendar)
    10. 그룹 스케줄 추가: 그룹에 일정 추가 (POST /api/v1/group/{groupId}/schedule)
    11. 그룹 스케줄 수정: 그룹 일정 수정 (PUT /api/v1/group/{groupId}/schedule/{scheduleId})
    12. 그룹 스케줄 삭제: 그룹 일정 삭제 (DELETE /api/v1/group/{groupId}/schedule/{scheduleId})
    */

    // ----------------- Group Service -----------
    @Operation(summary = "1.그룹 목록 조회", description = "회원이 속한 그룹 목록을 조회합니다.")
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

    @Operation(summary = "4. 그룹 참여 코드", description = "그룹의 참여코드 반환")
    @GetMapping("/code")
    public ResponseEntity<String> getGroupUniqueCode(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.validateGroupMaster(groupId, userId);
        String code = groupService.getGroupCode(groupId);

        return ResponseEntity.ok(code);
    }

    @Operation(summary = "4. 그룹 참여 코드", description = "그룹의 참여코드 반환")
    @PatchMapping("/reset-code")
    public ResponseEntity<String> resetGroupUniqueCode(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.validateGroupMaster(groupId, userId);
        String code = groupService.resetGroupCode(groupId);

        return ResponseEntity.ok("코드가 변경되었습니다: "+code);
    }

    //----------------- Group Member Service -----------------
    @Operation(summary = "4. 그룹 멤버 조회", description = "사이드바 하단용 그룹 멤버 목록을 조회합니다.")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupMemberService.getGroupMembers(groupId));
    }

    @Operation(summary = "5. 이메일 초대", description = "이메일을 통해 그룹에 멤버를 초대합니다.")
    @PostMapping("/{groupId}/invite/email")
    public ResponseEntity<String> inviteByEmail(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @RequestParam String email) {
        Long inviterId = Long.parseLong(userDetails.getUsername());
        groupMemberService.inviteByEmail(inviterId, groupId, email);
        return ResponseEntity.ok("이메일 초대가 완료되었습니다.");
    }

    @Operation(summary = "6. 친구 초대", description = "친구 목록 기반으로 그룹에 멤버를 초대합니다.")
    @PostMapping("/{groupId}/invite/friend/{friendId}")
    public ResponseEntity<String> inviteFriend(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @PathVariable Long friendId) {
        Long inviterId = Long.parseLong(userDetails.getUsername());
        groupMemberService.inviteFriend(inviterId, groupId, friendId);
        return ResponseEntity.ok("친구를 그룹에 초대했습니다.");
    }

    @Operation(summary = "7. 그룹 마스터 변경", description = "그룹의 방장을 다른 멤버로 변경합니다.")
    @PutMapping("/{groupId}/change-master/{newMasterId}")
    public ResponseEntity<Void> changeGroupMaster(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @PathVariable Long newMasterId) {

        Long userId = Long.parseLong(userDetails.getUsername());
        // 현재 마스터 확인
        groupService.validateGroupMaster(groupId, userId);
        // 새로운 마스터로 지정
        groupService.changeMaster(userId, groupId, newMasterId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "8. 그룹 나가기", description = "그룹 마스터는, change-master 이후에 나갈 수 있습니다.")
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        // 그룹 멤버 권한 확인 후 탈퇴 처리
        groupMemberService.leaveGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    // -------------- Group Schedule Service -----------
    @Operation(summary = "9. 그룹 캘린더 조회", description = "기간과 멤버 목록을 기반으로 병합된 그룹 캘린더 일정을 조회합니다.")
    @GetMapping("/{groupId}/calendar")
    public ResponseEntity<List<ConvergedScheduleDto>> getGroupCalendar(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @RequestBody GroupCalendarRequestDto request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupMemberService.isUserInGroup(groupId, userId);
        List<ConvergedScheduleDto> listreturn= scheduleConverge.convergeSchedules(request.getMemberIds(), request.getStartDate(), request.getEndDate());
        return ResponseEntity.ok(listreturn);
    }

    @Operation(summary = "10. 그룹 스케줄 추가", description = "해당 그룹에 단일 일정을 추가합니다.")
    @PostMapping("/{groupId}/new-schedule")
    public ResponseEntity<GroupScheduleDto> createGroupSchedule(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @RequestBody GroupScheduleCreateRequest request) {

        Long userId = Long.parseLong(userDetails.getUsername());
        // 1) 그룹 멤버인지 체크
        groupMemberService.isUserInGroup(groupId, userId);
        groupService.validateGroupMaster(groupId, userId);
        // 2) 일정 생성
        GroupScheduleDto dto = groupScheduleService.createSchedule(groupId, request);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "11. 그룹 스케줄 수정", description = "기존 그룹의 일정을 수정합니다.")
    @PutMapping("/{groupId}/schedule/{scheduleId}")
    public ResponseEntity<GroupScheduleDto> updateGroupSchedule(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @PathVariable Long scheduleId,
            @RequestBody GroupScheduleCreateRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());

        groupMemberService.isUserInGroup(groupId, userId);
        groupService.validateGroupMaster(groupId, userId);

        GroupScheduleDto dto = groupScheduleService.updateSchedule(groupId, scheduleId, request);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "12. 그룹 스케줄 삭제", description = "해당 그룹의 일정을 삭제합니다.")
    @DeleteMapping("/{groupId}/schedule/{scheduleId}")
    public ResponseEntity<Void> deleteGroupSchedule(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @PathVariable Long scheduleId) {
        Long userId = Long.parseLong(userDetails.getUsername());

        groupMemberService.isUserInGroup(groupId, userId);
        groupService.validateGroupMaster(groupId, userId);

        groupScheduleService.deleteSchedule(groupId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}

