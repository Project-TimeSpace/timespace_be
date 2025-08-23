package com.backend.User.Controller;

import java.util.List;
import java.util.Map;

import com.backend.User.Dto.InquiryRequestDto;
import com.backend.User.Dto.InquiryResponseDto;
import com.backend.User.Dto.UserInfoDto;
import com.backend.User.Dto.UserUpdateRequestDto;
import com.backend.User.Service.UserProfileAppService;
import com.backend.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('User')")    // = @PreAuthorize("hasAuthority('ROLE_User')")
@Tag(name = "1.마이페이지 api")
public class UserController {
    private final UserService userService;
    private final UserProfileAppService appService;

    /*
    1. 내 정보 조회 : 로그인된 사용자의 기본 정보(userName, email, university, major 등) 반환
    2. 내 정보 수정 : 사용자 프로필 정보(userName, university, major, phoneNumber, selfMemo 등) 수정
    3. 프로필 이미지 업로드/수정 : Multipart 파일로 프로필 이미지를 업로드하고 저장 경로 갱신

    4. 회원 탈퇴 : 사용자 계정 삭제 및 로그인 토큰 무효화 처리
    5. 연결된 소셜 계정 조회 : 로그인된 사용자의 소셜 로그인 연결 상태(Kakao, Google 등) 조회
    */

    @Operation(summary = "1. 내 정보 조회",
            description = "로그인된 사용자의 기본 정보(userName, email, university, major 등)를 반환합니다.")
    @GetMapping("/me")
    public UserInfoDto getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.getMyInfo(userId);
    }

    @Operation(summary="2. 내 정보 변경 요청", description="요청 접수만 하고, 관리자가 승인해야 반영됩니다.")
    @PatchMapping("/me")
    public ResponseEntity<String> submitMyInfoUpdate(@AuthenticationPrincipal UserDetails userDetails,
        @RequestBody UserUpdateRequestDto dto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userService.submitUpdateRequest(userId, dto);
        return ResponseEntity.ok("개인정보 수정 요청이 정상적으로 등록되었습니다.");
    }

    @Operation(summary = "3. 프로필 이미지 업로드/교체", description = "png/jpg/webp, ≤ 5MB")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadMyProfileImage(
        @AuthenticationPrincipal UserDetails ud,
        @RequestPart("file") MultipartFile file) throws Exception {

        Long userId = Long.parseLong(ud.getUsername());
        String url = appService.updateMyProfileImage(userId, file);
        return ResponseEntity.ok(Map.of("profileImageUrl", url));
    }

    @Operation(summary = "4. 내 프로필 이미지 삭제")
    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteMyProfileImage(@AuthenticationPrincipal UserDetails ud) {
        Long userId = Long.parseLong(ud.getUsername());
        appService.deleteMyProfileImage(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "5. 문의하기 생성", description = "앱 사용 관련 문의를 생성합니다. 진행 중인 문의가 있으면 등록이 불가합니다.")
    @PostMapping("/inquiries")
    public ResponseEntity<String> createInquiry(@AuthenticationPrincipal UserDetails userDetails,
        @RequestBody InquiryRequestDto dto) {

        Long userId = Long.parseLong(userDetails.getUsername());
        InquiryResponseDto response = userService.createInquiry(userId, dto);
        return ResponseEntity.ok("문의가 등록되었습니다.");
    }

    @Operation(summary = "6. 내 문의 내역 조회",
        description = "로그인한 사용자가 등록한 문의 목록을 생성일 내림차순으로 반환합니다.")
    @GetMapping("/inquiries")
    public ResponseEntity<List<InquiryResponseDto>> getMyInquiries(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<InquiryResponseDto> list = userService.getMyInquiries(userId);
        return ResponseEntity.ok(list);
    }

}
