package com.backend.User.Entity;



import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "`User`")  // 예약어이므로 백틱 사용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "유저 고유 ID", example = "1")
    private Long id;

    @Column(name = "user_name", nullable = false, length = 50)
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "이메일 주소", example = "hong@example.com")
    private String email;

    @Column(nullable = false, length = 100)
    @Schema(description = "비밀번호 (암호화 저장)", example = "$2a$10$...")
    private String password;

    @Column(length = 50)
    @Schema(description = "대학교 이름", example = "한양대학교")
    private String university;

    @Column(name = "phone_number", length = 20)
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;

    @Column(name = "kakao_id", length = 30)
    @Schema(description = "카카오톡 ID", example = "hong_kakao")
    private String kakaoId;

    @Column(name = "max_friend", nullable = false)
    @Schema(description = "최대 친구 수", example = "50")
    private Integer maxFriend = 50;

    @Column(name = "max_group", nullable = false)
    @Schema(description = "최대 그룹 수", example = "10")
    private Integer maxGroup = 10;

    @Column(name = "self_memo", length = 100)
    @Schema(description = "자기 메모", example = "자기소개 간단 메모")
    private String selfMemo;

    @Column(name = "birth_date")
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    @Column(name = "profile_image_url", length = 255)
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "가입일시", example = "2025-06-01T11:00:00")
    private LocalDateTime createdAt;

}
