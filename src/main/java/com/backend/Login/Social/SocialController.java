package com.backend.Login.Social;

import com.backend.Login.Dto.LoginResponseDto;
import com.backend.Login.Dto.SocialLoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SocialController {
    private final SocialService socialService;

    @PostMapping("/social/kakao")
    public ResponseEntity<LoginResponseDto> kakaoSocialLogin(@RequestBody SocialLoginRequestDto dto) {
        LoginResponseDto resp = socialService.loginWithKakao(dto.getAccessToken());
        return ResponseEntity.ok(resp);
    }
}
