package com.backend.Login.Auth;

import com.backend.Login.Dto.LoginRequestDto;
import com.backend.Login.Dto.LoginResponseDto;
import com.backend.Login.Dto.RegisterRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "1. AuthController")
public class AuthController {

     /*
     1. test API 호출: 인증 및 DB 연동 확인용
     2. 회원가입: 이메일·비밀번호 기반 회원 생성
     3. 로그인: 이메일·비밀번호 검증 후 AccessToken 및 RefreshToken 발급
     4. 재발급: RefreshToken 검증 후 새로운 AccessToken 발급
     5. 로그아웃: 현재 사용자의 RefreshToken 삭제
     6. 인증코드 발송: 이메일로 인증코드 전송
     7. 인증코드 확인: 이메일과 코드 일치 여부 검증
     8. 비밀번호 재설정: 인증코드 검증 후 새로운 비밀번호 저장
     */

    private final AuthService authService;

    @Operation(summary = "테스트 API", description = "인증 및 서버 연결 확인용")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test success");
    }

    @Operation(summary = "회원가입", description = "이메일과 비밀번호를 받아 새로운 회원을 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("Register Success");
    }

    @Operation(summary = "로그인", description = "사용자 이메일과 비밀번호를 검증하여 AccessToken과 RefreshToken을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto.getEmail(), requestDto.getPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "AccessToken 재발급", description = "유효한 RefreshToken을 받아 새로운 AccessToken을 발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody TokenRequestDto requestDto) {
        String newAccessToken = authService.reissueAccessToken(requestDto.getRefreshToken());
        return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
    }

    @Operation(summary = "로그아웃", description = "현재 사용자의 RefreshToken을 삭제하여 세션을 만료시킵니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인증코드 발송", description = "입력된 이메일로 인증코드를 전송합니다.")
    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerification(@RequestBody EmailRequestDto dto) throws MessagingException {
        authService.sendAuthEmail(dto.getEmail());
        return ResponseEntity.ok("인증코드 발송 완료");
    }

    @Operation(summary = "인증코드 검증", description = "이메일과 인증코드를 받아 일치 여부를 검증합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody EmailVerifyDto dto) {
        boolean result = authService.verifyEmailCode(dto.getEmail(), dto.getCode());
        if (result) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패");
        }
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일과 인증코드를 검증한 후 새로운 비밀번호로 업데이트합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDto dto) {
        boolean verified = authService.verifyEmailCode(dto.getEmail(), dto.getCode());
        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증코드가 일치하지 않습니다.");
        }

        authService.updatePassword(dto.getEmail(), dto.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
    }

    // -- DTO
    @Getter
    @Setter
    public static class EmailRequestDto {
        private String email;
    }

    @Getter
    @Setter
    public static class EmailVerifyDto {
        private String email;
        private String code;
    }

    @Getter @Setter
    public static class PasswordResetRequestDto {
        private String email;
        private String code;
        private String newPassword;
    }

    @Getter @Setter
    public static class TokenRequestDto {
        private String refreshToken;
    }

    @Getter @Setter @AllArgsConstructor
    public static class TokenResponseDto {
        private String accessToken;
    }

}
