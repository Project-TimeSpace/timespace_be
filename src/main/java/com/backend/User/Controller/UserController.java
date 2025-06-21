package com.backend.User.Controller;

import com.backend.User.Dto.UserInfoDto;
import com.backend.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")    // = @PreAuthorize("hasAuthority('ROLE_User')")
public class UserController {
    private final UserService userService;

    /*
    1. 내 정보 조회 : 로그인된 사용자의 기본 정보(userName, email, university, major 등) 반환
    2. 내 정보 수정 : 사용자 프로필 정보(userName, university, major, phoneNumber, selfMemo 등) 수정
    3. 프로필 이미지 업로드/수정 : Multipart 파일로 프로필 이미지를 업로드하고 저장 경로 갱신
    4. 회원 탈퇴 : 사용자 계정 삭제 및 로그인 토큰 무효화 처리
    5. 연결된 소셜 계정 조회 : 로그인된 사용자의 소셜 로그인 연결 상태(Kakao, Google 등) 조회
    */

    @Operation(summary = "내 정보 조회",
            description = "로그인된 사용자의 기본 정보(userName, email, university, major 등)를 반환합니다.")
    @GetMapping("/me")
    public UserInfoDto getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.getMyInfo(userId);
    }

    @Operation(summary = "내 정보 수정",
            description = "사용자의 프로필 정보를 수정합니다. null 값으로 전달된 항목은 기존 값을 유지합니다.")
    @PatchMapping("/me")
    public void updateMyInfo(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserInfoDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userService.updateMyInfo(userId, dto);
    }

}
