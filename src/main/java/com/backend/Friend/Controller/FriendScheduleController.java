package com.backend.Friend.Controller;

import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Dto.ScheduleRequestDto;
import com.backend.Friend.Service.FriendScheduleService;
import com.backend.SharedFunction.Converge.ConvergedScheduleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")
@Tag(name = "3-3. 친구 Schedule 기능 관련 api")
public class FriendScheduleController {

    private final FriendScheduleService friendScheduleService;

    // ---------- Friend Schedule Service ----------------
    @Operation(summary = "1. 친구 캘린더 조회(DTO 형태가 2가지라..부가설명 예정)", description = "특정 친구의 일정을 조회합니다.")
    @GetMapping("/{friendId}/calendar")
    public ResponseEntity<?> friendCalendar(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId, @RequestParam String startDate, @RequestParam String endDate) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendScheduleService.getFriendCalendar(userId, friendId, startDate, endDate));
    }

    // 2. 본인과 친구의 일정을 병합 조회
    @Operation(summary = "2. 친구 캘린더 병합 조회", description = "친구와 본인의 일정을 하나로 병합하여 조회합니다.")
    @GetMapping("/{friendId}/merge")
    public ResponseEntity<List<ConvergedScheduleDto>> mergedFriendCalendar(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId, @RequestParam String startDate, @RequestParam String endDate) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<ConvergedScheduleDto> merged =
                friendScheduleService.getMergedCalendar(userId, friendId, startDate, endDate);
        return ResponseEntity.ok(merged);
    }

    @Operation(summary = "3. 친구에게 약속 신청하기", description = "본인(friend_sender)과 친구(friend_receiver) 사이의 일정 요청을 생성합니다.")
    @PostMapping("/{friendId}/schedule/request")
    public ResponseEntity<String> sendScheduleRequest(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId, @RequestBody FriendScheduleRequestDto dto) {

        Long userId = Long.parseLong(userDetails.getUsername());
        friendScheduleService.sendScheduleRequest(userId, friendId, dto);
        return ResponseEntity.ok("약속을 신청했습니다.");
    }

    @Operation(summary = "4. 받은 약속 신청 조회", description = "친구들이 보낸 약속 신청 목록을 확인합니다.")
    @GetMapping("/schedules/requests")
    public ResponseEntity<List<ScheduleRequestDto>> getReceivedScheduleRequests(
        @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        List<ScheduleRequestDto> dtoList = friendScheduleService.getReceivedScheduleRequests(userId);
        return ResponseEntity.ok(dtoList);
    }

    // 4. 상대방이 보낸 약속 수락하기
    @Operation(summary = "5. 약속 수락하기", description = "친구가 보낸 일정 요청을 수락하여 실제 일정으로 등록합니다.")
    @PostMapping("/schedules/requests/{requestId}/accept")
    public ResponseEntity<String> acceptScheduleRequest(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendScheduleService.acceptScheduleRequest(userId, requestId);
        return ResponseEntity.ok("약속을 수락했습니다.");
    }

    // 5. 약속 거절하기
    @Operation(summary = "6. 약속 거절하기", description = "친구가 보낸 일정 요청을 거절하고 상태를 REJECTED로 업데이트합니다.->이거 그냥 알림 보내고 삭제로 할까..")
    @PostMapping("/schedules/requests/{requestId}/reject")
    public ResponseEntity<String> rejectScheduleRequest(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {

        Long userId = Long.parseLong(userDetails.getUsername());
        friendScheduleService.rejectScheduleRequest(userId, requestId);
        return ResponseEntity.ok("약속을 거절했습니다.");
    }


}
