package com.backend.group.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.group.Dto.GroupInviteResponse;
import com.backend.group.Service.GroupRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "4-3. 그룹 초대, 참여여부 수락 관련")
@PreAuthorize("hasRole('User')")
public class GroupRequestController {

	private final GroupRequestService groupRequestService;

	@Operation(summary = "1. 그룹 초대 받은 목록 조회(최신순)", description = "이 API로 수락 거절 진행")
	@GetMapping("/invite/received")
	public ResponseEntity<List<GroupInviteResponse>> getMyReceivedInvites(
		@AuthenticationPrincipal UserDetails userDetails) {
		Long userId = Long.parseLong(userDetails.getUsername());
		return ResponseEntity.ok(groupRequestService.getMyReceivedInvites(userId));
	}

	@Operation(summary = "2. 그룹 초대 수락", description = "그룹 초대 목록을 확인하고, 그룹 초대를 수락합니다.")
	@PostMapping("/{groupId}/invite/accept")
	public ResponseEntity<String> acceptInvite(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long groupId) {

		Long userId = Long.parseLong(userDetails.getUsername());
		groupRequestService.acceptInvite(userId, groupId);
		return ResponseEntity.ok("그룹 초대를 수락했습니다.");
	}

	@Operation(summary = "3. 그룹 초대 거절", description = "그룹 초대 목록을 확인하고, 그룹 초대를 거절합니다.")
	@PostMapping("/{groupId}/invite/decline")
	public ResponseEntity<String> declineInvite(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long groupId) {

		Long userId = Long.parseLong(userDetails.getUsername());
		groupRequestService.declineInvite(userId, groupId);
		return ResponseEntity.ok("그룹 초대를 거절했습니다.");
	}
}
