package com.backend.Friend.Controller;


import com.backend.Config.GlobalEnum;
import com.backend.Config.GlobalEnum.SortOption;
import com.backend.Friend.Dto.FriendDto;
import com.backend.Friend.Dto.FriendRequestReceivedDto;
import com.backend.Friend.Dto.FriendScheduleRequestDto;
import com.backend.Friend.Service.FriendRequestService;
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

    /*
    @Operation(summary = "1. 친구 목록 조회", description = "로그인한 사용자의 모든 친구 목록(이름, 즐겨찾기 여부, 별칭 등)을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<FriendDto>> listFriends(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendService.getFriends(userId));
    }*/

    @Operation(summary = "1. 친구 목록 조회", description = "로그인한 사용자의 모든 친구 목록 반환. ")
    @GetMapping("/{sort}")
    public ResponseEntity<List<FriendDto>> friendsList(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("sort") SortOption sortOption) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendService.getFriends(userId, sortOption));
    }


    // 즐겨찾기 토글
    @PatchMapping("/{friendId}/favorite")
    public ResponseEntity<Void> setFavorite(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId, @RequestParam boolean favorite) {
        friendService.updateFavorite(Long.parseLong(userDetails.getUsername()), friendId, favorite);
        return ResponseEntity.ok().build();
    }

    // 공개범위 변경
    @PatchMapping("/{friendId}/visibility")
    public ResponseEntity<Void> setVisibility(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId, @RequestParam GlobalEnum.Visibility visibility) {
        friendService.updateVisibility(Long.parseLong(userDetails.getUsername()), friendId, visibility);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "2. 친구 요청 보내기", description = "path variable 로 전달된 이메일을 통해 다른 사용자에게 친구 요청을 보냅니다.")
    @PostMapping("/requests/{email}")
    public ResponseEntity<Void> sendRequest(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String email) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendRequestService.sendFriendRequest(userId, email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3. 받은 친구 요청 조회", description = "로그인한 사용자가 받은 친구 요청 목록을 반환합니다.")
    @GetMapping("/requests/received")
    public ResponseEntity<List<FriendRequestReceivedDto>> receivedRequests(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendRequestService.getReceivedRequests(userId));
    }

    @Operation(summary = "4. 친구 요청 수락하기",
            description = "특정 친구 요청을 수락하여 친구 관계를 생성합니다.")
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendRequestService.acceptFriendRequest(userId, requestId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "5. 친구 요청 거절하기",
            description = "특정 친구 요청을 거절합니다.")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendRequestService.rejectFriendRequest(userId, requestId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "6. 친구 캘린더 조회", description = "특정 친구의 일정을 조회합니다.")
    @GetMapping("/{friendId}/calendar")
    public ResponseEntity<?> friendCalendar(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId, @RequestParam String startDate, @RequestParam String endDate) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendService.getFriendCalendar(userId, friendId, startDate, endDate));
    }
}
