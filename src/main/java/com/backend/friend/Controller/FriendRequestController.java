package com.backend.friend.Controller;

import com.backend.friend.Dto.FriendRequestReceivedDto;
import com.backend.friend.Service.FriendRequestService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")
@Tag(name = "3-2. 친구 요청 기능 관련 api")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    // ---------- friend Request Service ----------------
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
}
