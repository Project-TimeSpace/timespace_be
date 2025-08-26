package com.backend.group.Controller;

import com.backend.configenum.GlobalEnum;
import com.backend.group.Dto.GroupCreateRequestDto;
import com.backend.group.Dto.GroupInfoDto;
import com.backend.group.Dto.GroupMemberDto;
import com.backend.group.Dto.GroupSummaryDto;
import com.backend.group.Service.GroupMemberService;
import com.backend.group.Service.GroupScheduleService;
import com.backend.group.Service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "4-1. 그룹 관련 api")
@PreAuthorize("hasRole('User')")
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupScheduleService groupScheduleService;

    // ----------------- group Service -----------
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

    @Operation(summary = "3. 그룹 멤버 조회", description = "사이드바 하단용 그룹 멤버 목록을 조회합니다.")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupMemberService.isUserInGroup(groupId, userId);
        return ResponseEntity.ok(groupMemberService.getGroupMembers(groupId));
    }

    @Operation(summary = "4. 그룹 생성", description = "새로운 그룹을 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GroupCreateRequestDto request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.createGroup(userId, request);
        return ResponseEntity.ok("그룹이 생성되었습니다");
    }

    @Operation(
        summary = "4-1. 그룹 생성 (멀티파트)",
        description = "이미지를 함께 업로드할 수 있습니다. form-data로 전송: " +
            "`request`=JSON, `image`=파일(선택)."
    )
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createGroupMultipart(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestPart("request") GroupCreateRequestDto request,
        @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.createGroup(userId, request, image);
        return ResponseEntity.ok("그룹이 생성되었습니다");
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

    @Operation(summary = "6. 그룹 일정 나의 상태 변경", description = "status = PENDING | ACCEPTED | REJECTED")
    @PutMapping("/{groupId}/schedule/{scheduleId}/{status}")
    public ResponseEntity<String> changeMyScheduleStatus(@AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long groupId, @PathVariable Long scheduleId, @PathVariable GlobalEnum.RequestStatus status
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupMemberService.isUserInGroup(groupId, userId);
        groupScheduleService.updateMyStatus(userId, groupId, scheduleId, status);
        return ResponseEntity.ok("그룹의 스케쥴 상태를 변경했습니다.");
    }
}

