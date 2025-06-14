package com.backend.Login.Social;

import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoUserInfo {
    private final String kakaoId;
    private final String nickname;
    private final String email;

    @SuppressWarnings("unchecked")
    public KakaoUserInfo(Map<String, Object> attributes) {
        // 1) 최상위 “id” 필드
        this.kakaoId = attributes.get("id").toString();

        // 2) properties 맵에서 “nickname” 가져오기
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        this.nickname = (String) properties.get("nickname");

        // 3) kakao_account 아래에서 “email” 가져오기
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.email = (String) kakaoAccount.get("email");  // null일 수도 있음
    }
}

