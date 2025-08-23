package com.backend.Group.Controller;

import java.util.Map;

import com.backend.Group.Service.GroupImageAppService;
import com.backend.Group.Service.GroupMemberService;
import com.backend.Group.Service.GroupScheduleService;
import com.backend.Group.Service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "4-2. 그룹 방장이 사용가능한 멤버 관리 api")
@PreAuthorize("hasRole('User')")
public class GroupMasterController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupImageAppService groupImageAppService;

    @Operation(summary = "1. 그룹 참여 코드", description = "그룹의 참여코드 반환")
    @GetMapping("/{groupId}/code")
    public ResponseEntity<String> getGroupUniqueCode(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.validateGroupMaster(groupId, userId);
        String code = groupService.getGroupCode(groupId);

        return ResponseEntity.ok(code);
    }

    @Operation(summary = "1-2. 그룹 참여 코드 리셋", description = "그룹의 참여코드 초기화")
    @PatchMapping("/{groupId}/reset-code")
    public ResponseEntity<String> resetGroupUniqueCode(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        groupService.validateGroupMaster(groupId, userId);
        String code = groupService.resetGroupCode(groupId);

        return ResponseEntity.ok("코드가 변경되었습니다: "+code);
    }

    @Operation(summary = "2. 이메일 초대", description = "이메일을 통해 그룹에 멤버를 초대합니다.")
    @PostMapping("/{groupId}/invite/email")
    public ResponseEntity<String> inviteByEmail(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @RequestParam String email) {
        Long inviterId = Long.parseLong(userDetails.getUsername());
        groupService.validateGroupMaster(groupId, inviterId);
        groupMemberService.inviteByEmail(inviterId, groupId, email);
        return ResponseEntity.ok("이메일 초대가 완료되었습니다.");
    }

    @Operation(summary = "3. 친구 초대", description = "친구 목록 기반으로 그룹에 멤버를 초대합니다.")
    @PostMapping("/{groupId}/invite/friend/{friendId}")
    public ResponseEntity<String> inviteFriend(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId, @PathVariable Long friendId) {
        Long inviterId = Long.parseLong(userDetails.getUsername());
        groupMemberService.inviteFriend(inviterId, groupId, friendId);
        return ResponseEntity.ok("친구를 그룹에 초대했습니다.");
    }

    @Operation(summary = "4. 그룹 마스터 변경", description = "그룹의 방장을 다른 멤버로 변경합니다.")
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

    @Operation(summary = "그룹 프로필 이미지 업로드/교체", description = "그룹 마스터만 가능. png/jpg/webp, ≤ 5MB")
    @PostMapping(value = "/{groupId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadGroupProfileImage(
        @AuthenticationPrincipal UserDetails ud, @PathVariable Long groupId,
        @RequestPart("file") MultipartFile file) throws Exception {

        Long userId = Long.parseLong(ud.getUsername());
        groupService.validateGroupMaster(groupId, userId);
        String url = groupImageAppService.updateGroupProfileImage(groupId, file);
        return ResponseEntity.ok(Map.of("groupImageUrl", url));
    }

    @Operation(summary = "그룹 프로필 이미지 삭제", description = "그룹 마스터만 가능")
    @DeleteMapping("/{groupId}/profile-image")
    public ResponseEntity<Void> deleteGroupProfileImage(
        @AuthenticationPrincipal UserDetails ud,
        @PathVariable Long groupId) {

        Long userId = Long.parseLong(ud.getUsername());
        groupService.validateGroupMaster(groupId, userId);
        groupImageAppService.deleteGroupProfileImage(groupId);
        return ResponseEntity.noContent().build();
    }
}
