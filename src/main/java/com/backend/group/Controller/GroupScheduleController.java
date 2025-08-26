package com.backend.group.Controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.group.Dto.GroupScheduleCreateRequest;
import com.backend.group.Dto.GroupScheduleDto;
import com.backend.group.Service.GroupMemberService;
import com.backend.group.Service.GroupScheduleService;
import com.backend.group.Service.GroupService;
import com.backend.shared.Converge.ConvergedScheduleDto;
import com.backend.shared.Converge.ScheduleConverge;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "4-4. 그룹 일정 관련 모든 API -> 이준민 담당")
@PreAuthorize("hasRole('User')")
public class GroupScheduleController {

	private final GroupService groupService;
	private final GroupMemberService groupMemberService;
	private final GroupScheduleService groupScheduleService;
	private final ScheduleConverge scheduleConverge;

	@Operation(summary = "1. 그룹멤버 공통 - 그룹 멤버의 병합된 캘린더 조회", description = "기간과 멤버 목록을 기반으로 병합된 그룹 캘린더 일정을 조회합니다.")
	@GetMapping("/{groupId}/calendar")
	public ResponseEntity<List<ConvergedScheduleDto>> getGroupCalendar(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long groupId,
		@RequestParam String startDate, @RequestParam String endDate, @RequestParam List<Long> memberIds) {
		Long userId = Long.parseLong(userDetails.getUsername());
		groupMemberService.isUserInGroup(groupId, userId);
		List<ConvergedScheduleDto> listreturn= scheduleConverge.convergeSchedules(memberIds, startDate, endDate);
		return ResponseEntity.ok(listreturn);
	}

	@Operation(summary = "2. 그룹멤버 공통 - 그룹 스케줄 가져오기", description = "해당 그룹에서 추가된 모든 일정을 Get합니다.")
	@GetMapping("/{groupId}/schedule")
	public ResponseEntity<List<GroupScheduleDto>> getGroupSchedules(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long groupId){
		Long userId = Long.parseLong(userDetails.getUsername());
		groupMemberService.isUserInGroup(groupId, userId);
		List<GroupScheduleDto> schedules = groupScheduleService.getGroupSchedules(groupId);
		return ResponseEntity.ok(schedules);
	}

	@Operation(summary = "3. 그룹 마스터가 - 그룹 스케줄 추가", description = "마스터 권한. 해당 그룹에 단일 일정을 추가합니다.")
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

	@Operation(summary = "4. 그룹마스터가 - 그룹의 스케줄 수정", description = "일정 필드 수정 후, (만약 날짜나 시간 변경인 경우) 해당 일정의 ACCEPTED 사용자들을 PENDING으로 초기화하고 알림을 전송합니다. 이름이나 다른 필드 바꾼건 그대로")
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

	@Operation(summary = "5. 그룹 마스터가 - 그룹 스케줄 삭제", description = "해당 그룹의 일정을 삭제합니다. 모든 유저한테도 삭제됨.")
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
