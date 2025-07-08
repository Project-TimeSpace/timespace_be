package com.backend.Friend.Controller;


import com.backend.ConfigEnum.GlobalEnum.SortOption;
import com.backend.Converge.ConvergedScheduleDto;
import com.backend.Friend.Dto.FriendDto;
import com.backend.Friend.Dto.FriendRequestReceivedDto;
import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Service.FriendRequestService;
import com.backend.Friend.Service.FriendScheduleService;
import com.backend.Friend.Service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/friends")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")
public class FriendController {

    /*
    1. 친구 목록 조회               : 로그인한 사용자의 모든 친구 목록 조회
    2. 친구 요청 보내기            : 이메일을 통해 사용자에게 친구 요청 전송
    3. 받은 친구 요청 조회         : 로그인 사용자가 받은 친구 요청 목록 조회
    4. 친구 요청 수락하기          : 특정 친구 요청을 수락하여 친구 관계 생성
    5. 친구 요청 거절하기          : 특정 친구 요청을 거절
    6. 친구 캘린더 조회            : 특정 친구의 일정을 조회
    */

    private final FriendService friendService;
    private final FriendRequestService friendRequestService;
    private final FriendScheduleService friendScheduleService;

    /*
    @Operation(summary = "1. 친구 목록 조회", description = "로그인한 사용자의 모든 친구 목록(이름, 즐겨찾기 여부, 별칭 등)을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<FriendDto>> listFriends(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendService.getFriends(userId));
    }*/


    // ------------------ Friend Service --------------------
    @Operation(summary = "1. 친구 목록 조회", description = "로그인한 사용자의 모든 친구 목록 반환. ")
    @GetMapping("/list/{sort}")
    public ResponseEntity<List<FriendDto>> friendsList(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("sort") SortOption sortOption) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendService.getFriends(userId, sortOption));
    }

    // 즐겨찾기 토글
    @Operation(summary = "2. 친구 즐겨찾기 설정", description = "상태 변경True->False , False->True.")
    @PatchMapping("/{friendId}/favorite")
    public ResponseEntity<Void> setFavorite(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long friendId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendService.updateFavorite(userId, friendId);
        return ResponseEntity.ok().build();
    }

    // 공개범위 변경
    @Operation(summary = "3. 친구에게 캘린더 공개범위 변경", description = "상태 변경. ALL->SIMPLE->SECRET->ALL")
    @PatchMapping("/{friendId}/visibility")
    public ResponseEntity<Void> setVisibility(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long friendId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendService.updateVisibility(userId, friendId);
        return ResponseEntity.ok().build();
    }

    // ---------- Friend Request Service ----------------
    @Operation(summary = "1. 친구 요청 보내기", description = "path variable 로 전달된 이메일을 통해 다른 사용자에게 친구 요청을 보냅니다.")
    @PostMapping("/requests/{email}")
    public ResponseEntity<Void> sendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String email) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendRequestService.sendFriendRequest(userId, email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "2. 받은 친구 요청 조회", description = "로그인한 사용자가 받은 친구 요청 목록을 반환합니다.")
    @GetMapping("/requests/received")
    public ResponseEntity<List<FriendRequestReceivedDto>> receivedRequests(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendRequestService.getReceivedRequests(userId));
    }

    @Operation(summary = "3. 친구 요청 수락하기", description = "특정 친구 요청을 수락하여 친구 관계를 생성합니다.")
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendRequestService.acceptFriendRequest(userId, requestId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "4. 친구 요청 거절하기", description = "특정 친구 요청을 거절합니다.")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendRequestService.rejectFriendRequest(userId, requestId);
        return ResponseEntity.ok().build();
    }

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

    // 4. 상대방이 보낸 약속 수락하기
    @Operation(summary = "4. 약속 수락하기", description = "친구가 보낸 일정 요청을 수락하여 실제 일정으로 등록합니다.")
    @PostMapping("/schedules/requests/{requestId}/accept")
    public ResponseEntity<String> acceptScheduleRequest(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendScheduleService.acceptScheduleRequest(userId, requestId);
        return ResponseEntity.ok("약속을 수락했습니다.");
    }

    // 5. 약속 거절하기
    @Operation(summary = "5. 약속 거절하기", description = "친구가 보낸 일정 요청을 거절하고 상태를 REJECTED로 업데이트합니다.->이거 그냥 알림 보내고 삭제로 할까..")
    @PostMapping("/schedules/requests/{requestId}/reject")
    public ResponseEntity<String> rejectScheduleRequest(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {

        Long userId = Long.parseLong(userDetails.getUsername());
        friendScheduleService.rejectScheduleRequest(userId, requestId);
        return ResponseEntity.ok("약속을 거절했습니다.");
    }


}
