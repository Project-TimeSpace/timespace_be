package com.backend.friend.Controller;


import com.backend.configenum.GlobalEnum;
import com.backend.configenum.GlobalEnum.SortOption;
import com.backend.friend.Dto.FriendNicknameUpdate;
import com.backend.friend.Dto.FriendDto;
import com.backend.friend.Service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")
@Tag(name = "3-1. 친구 기능 관련 api")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "1. 친구 목록 조회", description = "로그인한 사용자의 모든 친구 목록 반환. ")
    @GetMapping("/list/{sort}")
    public ResponseEntity<List<FriendDto>> friendsList(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("sort") SortOption sortOption) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(friendService.getFriends(userId, sortOption));
    }

    // 이거 수정해야해
    @Operation(summary = "2. 친구 즐겨찾기 설정", description = "상태 변경True->False , False->True.")
    @PatchMapping("/{friendId}/favorite")
    public ResponseEntity<Void> setFavorite(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long friendId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendService.updateFavorite(userId, friendId);
        return ResponseEntity.ok().build();
    }

    // 공개범위 변경
    @Operation(summary = "3. 친구에게 캘린더 공개범위 변경", description = "상태 변경. ALL,SIMPLE,SECRET")
    @PatchMapping("/{friendId}/{visibility}")
    public ResponseEntity<Void> setVisibility(@AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long friendId, @PathVariable GlobalEnum.Visibility visibility) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendService.updateVisibility(userId, friendId, visibility);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "4. 친구 닉네임 수정",
        description = "로그인한 사용자가 지정한 친구(user ↔ friend 관계)의 닉네임을 변경합니다.")
    @PatchMapping("/{friendUserId}/nickname")
    public ResponseEntity<FriendNicknameUpdate> updateNickname(@AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long friendUserId, @RequestBody FriendNicknameUpdate request) {

        Long userId = Long.parseLong(userDetails.getUsername());
        FriendNicknameUpdate result = friendService.updateNickname(userId, friendUserId, request.getNickname());
        return ResponseEntity.ok(result);
    }

    // 이것도 수정
    @Operation(summary = "5. 친구 삭제", description = "특정 친구 관계를 삭제합니다.")
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        friendService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }



}
