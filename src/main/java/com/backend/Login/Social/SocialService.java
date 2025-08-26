package com.backend.Login.Social;

import com.backend.configsecurity.JwtTokenProvider;
import com.backend.Login.Dto.LoginResponseDto;
import com.backend.Login.SocialAccount;
import com.backend.Login.SocialAccountRepository;
import com.backend.user.Entity.User;
import com.backend.user.Repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class SocialService {
    private final WebClient webClient;
    private final JwtTokenProvider jwtProvider;
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;

    private static final String KAKAO_USERINFO_URL = "/v2/user/me";

    @Transactional
    public LoginResponseDto loginWithKakao(String accessToken) {
        // 1) 카카오 API 호출해서 Map 형태로 유저 정보 가져오기
        Map<String, Object> attributes = webClient.get()
                .uri(KAKAO_USERINFO_URL)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        // 2) 도메인으로 변환
        KakaoUserInfo info = new KakaoUserInfo(attributes);
        String kakaoId = info.getKakaoId();

        // 3) SocialAccount 조회 (provider = "kakao")
        SocialAccount account = socialAccountRepository
                .findByProviderAndProviderUserId("kakao", kakaoId)
                .orElseGet(() -> {
                    // 3-1) user 신규 생성
                    User newUser = User.builder()
                            .email(info.getEmail())
                            .userName(info.getNickname())
                            .build();
                    newUser = userRepository.save(newUser);

                    // 3-2) SocialAccount 신규 생성
                    SocialAccount sa = SocialAccount.builder()
                            .user(newUser)
                            .provider("kakao")
                            .providerUserId(kakaoId)
                            .build();
                    return socialAccountRepository.save(sa);
                });

        // 4) JWT 생성 및 반환
        User user = account.getUser();
        String jwt = jwtProvider.createToken(user.getId(), "user");
        return LoginResponseDto.builder()
                .token(jwt)
                .type("user")
                .build();
    }

}
